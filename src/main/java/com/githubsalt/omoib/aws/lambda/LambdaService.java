package com.githubsalt.omoib.aws.lambda;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.githubsalt.omoib.aws.dto.MaskingLambdaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class LambdaService {

    private final AWSLambda awsLambda;

    public void invokeLambdaAsync(String functionName, MaskingLambdaDTO payload) {

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString;


        try {
            jsonString = nest(objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        log.info("Invoke Lambda function: {}, payload: {}", functionName, jsonString);
        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(functionName)
                .withPayload(jsonString)
                .withInvocationType(InvocationType.Event); // 비동기 호출

        log.info("InvokeRequest: {}", invokeRequest);
        log.info("InvokeRequest getPayload: {}", new String(invokeRequest.getPayload().array(), StandardCharsets.UTF_8));
        InvokeResult invokeResult = awsLambda.invoke(invokeRequest);
        log.info("InvokeResult: {}", invokeResult);
        // 결과 처리 로직은 필요없음 (비동기 호출이므로 즉시 반환)
    }

    static String nest(String originalJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        // 기존 JSON 데이터를 ObjectNode로 파싱
        ObjectNode originalNode = (ObjectNode) objectMapper.readTree(originalJson);

        // 새 JSON 구조 생성
        ObjectNode newJson = objectMapper.createObjectNode();
        newJson.set("body-json", originalNode);

        // 변환된 JSON 출력
        return objectMapper.writeValueAsString(newJson);
    }
}
