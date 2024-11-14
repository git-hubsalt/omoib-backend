package com.githubsalt.omoib.global.config.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sagemakerruntime.AmazonSageMakerRuntime;
import com.amazonaws.services.sagemakerruntime.AmazonSageMakerRuntimeClientBuilder;
import com.amazonaws.services.sagemakerruntime.model.InvokeEndpointRequest;
import com.amazonaws.services.sagemakerruntime.model.InvokeEndpointResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.ByteBuffer;

@Component
public class SageMakerInvoker {

    private AmazonSageMakerRuntime sageMakerRuntime;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;
    @Value("${cloud.aws.region.static}")
    private String region;

    // @PostConstruct를 사용하여 초기화 메서드를 정의
    @PostConstruct
    private void init() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        this.sageMakerRuntime = AmazonSageMakerRuntimeClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    public String invokeEndpoint(String endpoint, Object payload) {
        try {
            // JSON 직렬화를 위한 payload 변환
            String jsonPayload = objectMapper.writeValueAsString(payload);

            // InvokeEndpointRequest 설정
            InvokeEndpointRequest request = new InvokeEndpointRequest()
                    .withEndpointName(endpoint)
                    .withContentType("application/json")
                    .withBody(ByteBuffer.wrap(jsonPayload.getBytes()));

            // 엔드포인트 호출 및 결과 받기
            InvokeEndpointResult result = sageMakerRuntime.invokeEndpoint(request);

            // 결과를 문자열로 반환
            return new String(result.getBody().array());

        } catch (Exception e) {
            throw new RuntimeException("Error invoking SageMaker endpoint", e);
        }
    }
}