package com.githubsalt.omoib.aws;

import com.amazonaws.DefaultRequest;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

@Component
public class AwsRequestSigner {

    private final AWSCredentials credentials;
    private final AWS4Signer signer;
    private final ObjectMapper objectMapper;

    public AwsRequestSigner(
            @Value("${cloud.aws.credentials.access-key}") String accessKey,
            @Value("${cloud.aws.credentials.secret-key}") String secretKey,
            @Value("${cloud.aws.region.static}") String region
    ) {
        this.credentials = new BasicAWSCredentials(accessKey, secretKey);
        this.signer = new AWS4Signer();
        this.signer.setServiceName("sagemaker"); // 필요에 따라 변경 가능
        this.signer.setRegionName(region);
        this.objectMapper = new ObjectMapper();
    }

    public <T> HttpHeaders signRequest(T dto, String url) throws Exception {
        // 1. DefaultRequest 설정
        DefaultRequest<Void> signableRequest = new DefaultRequest<>("sagemaker");
        signableRequest.setHttpMethod(com.amazonaws.http.HttpMethodName.POST);
        signableRequest.setEndpoint(new URI(url));

        // 2. 요청 본문 설정
        byte[] content = objectMapper.writeValueAsBytes(dto);
        signableRequest.setContent(new ByteArrayInputStream(content));

        // 3. Content-Type 헤더 추가
        signableRequest.addHeader("Content-Type", "application/json");

        // 4. x-amz-content-sha256 헤더 설정
        String contentHash = DigestUtils.sha256Hex(content);
        signableRequest.addHeader("x-amz-content-sha256", contentHash);

        // 5. Canonical Request와 String-to-Sign 생성 및 로깅
        signer.sign(signableRequest, credentials);

        // 6. 서명된 헤더를 HttpHeaders에 복사
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        for (Map.Entry<String, String> entry : signableRequest.getHeaders().entrySet()) {
            headers.add(entry.getKey(), entry.getValue());
        }

        return headers;
    }

    private String getAmzDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("KST"));
        return dateFormat.format(date);
    }
}