package com.githubsalt.omoib.aws.lambda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.githubsalt.omoib.aws.dto.MaskingLambdaDTO;
import com.githubsalt.omoib.aws.dto.MaskingLambdaRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@Slf4j
@RequestMapping("/lambda")
@RequiredArgsConstructor
public class LambdaController {

    private final LambdaService lambdaService;

    // Lambda 콜백 처리 API
    @PostMapping("/callback")
    public void handleLambdaCallback(@RequestBody String result) {
        // Lambda 결과 처리 로직

        // TODO 콜백 처리 시 결과 필드의 인덱스나 네임 필드를 읽고, 그에 해당하는 서비스로 결과를 전달하는 방식으로 구현할듯
        log.info("Received Lambda result: {}", result);
    }

    // Lambda 호출 테스트용 API
    @PostMapping("/invoke")
    public void invoke(@RequestParam(name = "functionName") String functionName, @RequestBody MaskingLambdaRequestDTO dto) {
        MaskingLambdaDTO maskingLambdaDTO = getMaskingLambdaDTO(dto);
        log.info("Lambda call test with RequestBody : {}", dto);
        lambdaService.invokeLambdaAsync(functionName, maskingLambdaDTO);
    }

    @Deprecated
    @PostMapping("/invoke/{lambdaUrl}")
    public HttpResponse<String> invokeLambdaUrl(@PathVariable String lambdaUrl, @RequestBody MaskingLambdaRequestDTO dto) {
        log.info("Lambda call test to lambdaUrl {} with RequestBody : {}", lambdaUrl, dto);
        MaskingLambdaDTO maskingLambdaDTO = getMaskingLambdaDTO(dto);

        String url = "https://" + lambdaUrl + "/";
        log.info("Request URL: {}", url);
        try {
            // HttpClient 인스턴스 생성
            HttpClient client = HttpClient.newHttpClient();

            // JSON 데이터 생성
            var objectMapper = new ObjectMapper();
            String jsonData = LambdaService.nest(objectMapper.writeValueAsString(maskingLambdaDTO));
            log.info("Request JSON: {}", jsonData);

            // HttpRequest 생성
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                    .build();
            log.info("Request: {}", request);

            // HttpResponse 받기
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            log.error("Error occurred while sending request to lambdaUrl: {}", lambdaUrl, e);
        }
        return null;
    }

    private static MaskingLambdaDTO getMaskingLambdaDTO(MaskingLambdaRequestDTO dto) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd-HHmmss");
        String formattedNow = now.format(formatter);
        MaskingLambdaDTO maskingLambdaDTO = new MaskingLambdaDTO(dto.username(), dto.row_image_url(), formattedNow);
        return maskingLambdaDTO;
    }

}
