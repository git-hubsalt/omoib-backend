package com.githubsalt.omoib.aws.lambda;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.InvocationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LambdaService {

    private final AWSLambda awsLambda;

    public void invokeLambdaAsync(String functionName, String payload) {
        log.info("Invoke Lambda function: {}, payload: {}", functionName, payload);
        InvokeRequest invokeRequest = new InvokeRequest()
            .withFunctionName(functionName)
            .withPayload(payload)
            .withInvocationType(InvocationType.Event); // 비동기 호출

        log.info("InvokeRequest: {}", invokeRequest);
        InvokeResult invokeResult = awsLambda.invoke(invokeRequest);
        log.info("InvokeResult: {}", invokeResult);
        // 결과 처리 로직은 필요없음 (비동기 호출이므로 즉시 반환)
    }
}
