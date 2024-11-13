package com.githubsalt.omoib.virtualfitting;

import com.amazonaws.DefaultRequest;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.HttpMethodName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.githubsalt.omoib.aws.AwsRequestSigner;
import com.githubsalt.omoib.aws.s3.PresignedURLBuilder;
import com.githubsalt.omoib.clothes.domain.Clothes;
import com.githubsalt.omoib.clothes.service.ClothesService;
import com.githubsalt.omoib.history.History;
import com.githubsalt.omoib.history.HistoryService;
import com.githubsalt.omoib.history.HistoryType;
import com.githubsalt.omoib.history.enums.HistoryStatus;
import com.githubsalt.omoib.user.domain.User;
import com.githubsalt.omoib.user.service.UserService;
import com.githubsalt.omoib.virtualfitting.dto.FittingAIRequestDTO;
import com.githubsalt.omoib.virtualfitting.dto.FittingRequestDTO;
import com.githubsalt.omoib.virtualfitting.dto.SqsFittingResponseMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class VirtualFittingService {

    private final ClothesService clothesService;
    private final HistoryService historyService;
    private final UserService userService;
    private final PresignedURLBuilder presignedURLBuilder;
    private final AwsRequestSigner awsRequestSigner;
    private final RestTemplate restTemplate;

    private final String fitting_endpoint = "https://runtime.sagemaker.ap-northeast-2.amazonaws.com/endpoints/vton-new-one/invocations";

    public ResponseEntity<String> fitting(Long userId, FittingRequestDTO requestDTO) {

        if (historyService.hasPendingHistory(userId)) {
            throw new IllegalStateException("이미 피팅 요청이 진행 중입니다.");
        }

        Clothes upper = clothesService.getClothes(requestDTO.upperClothesId());
        Clothes lower = clothesService.getClothes(requestDTO.lowerClothesId());

        if (upper == null || lower == null) {
            throw new IllegalArgumentException("상의와 하의를 모두 선택해주세요.");
        }
        String timestamp = historyService.createPendingFittingHistory(userId, new ArrayList<>(List.of(upper, lower)));

        User user = userService.findUser(userId).orElseThrow();
        FittingAIRequestDTO aiRequestDTO = new FittingAIRequestDTO(
                userId.toString(),
                timestamp,
                presignedURLBuilder.buildGetPresignedURL(user.getRowImagePath()).toString(),
                presignedURLBuilder.buildGetPresignedURL(upper.getImagePath()).toString(),
                presignedURLBuilder.buildGetPresignedURL(lower.getImagePath()).toString(),
                presignedURLBuilder.buildGetPresignedURL(String.format("users/%s/masking/%s/overall.png",
                        1234/*user.getSocialId()*/, user.getLastMaskingTimestamp())).toString(), // TODO rollback
                "overall");


        // 1. awsRequestSigner를 통해 서명된 HttpHeaders 생성
        HttpHeaders headers;
        try {
            headers = awsRequestSigner.signRequest(aiRequestDTO, fitting_endpoint);
        } catch (Exception e) {
            throw new RuntimeException("AWS 요청 서명 중 오류 발생", e);
        }

        // 2. RestTemplate 요청 생성 및 전송
        HttpEntity<FittingAIRequestDTO> requestEntity = new HttpEntity<>(aiRequestDTO, headers);
        return restTemplate.exchange(fitting_endpoint, HttpMethod.POST, requestEntity, String.class);
    }

    public ResponseEntity<String> sendFittingRequest(FittingAIRequestDTO aiRequestDTO, String url) {
        // 1. AWS 자격 증명 설정
        AWSCredentials credentials = new BasicAWSCredentials("ACCESS_KEY", "SECRET_KEY");

        // 2. AWS4Signer 설정
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName("sagemaker");
        signer.setRegionName("region");

        // 3. SignableRequest 생성
        DefaultRequest<Void> signableRequest = new DefaultRequest<>("sagemaker");
        signableRequest.setHttpMethod(HttpMethodName.POST);
        signableRequest.setEndpoint(URI.create(url));
        try {
            signableRequest.setContent(new ByteArrayInputStream(new ObjectMapper().writeValueAsBytes(aiRequestDTO)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 4. 서명 수행
        signer.sign(signableRequest, credentials);

        // 5. SignableRequest에서 서명된 헤더를 HttpHeaders로 변환
        HttpHeaders headers = new HttpHeaders();
        for (Map.Entry<String, String> entry : signableRequest.getHeaders().entrySet()) {
            headers.add(entry.getKey(), entry.getValue());
        }

        // 6. RestTemplate 요청 전송
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<FittingAIRequestDTO> requestEntity = new HttpEntity<>(aiRequestDTO, headers);

        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
    }

    public void response(SqsFittingResponseMessageDTO message) {
        History pendingHistory = historyService.findPendingHistory(Long.parseLong(message.userId()), HistoryType.FITTING);
        pendingHistory.setFittingImageURL("/users/" + message.userId() + "/vton_result/" + message.timestamp() + "result.jpg");
        pendingHistory.setStatus(HistoryStatus.COMPLETED);
        historyService.updateHistory(pendingHistory);
    }
}
