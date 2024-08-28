package com.githubsalt.omoib.aws.lambda;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.InvocationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LambdaService {

    private final AWSLambda awsLambda;

    public void invokeLambdaAsync(String functionName, String payload) {
        InvokeRequest invokeRequest = new InvokeRequest()
            .withFunctionName(functionName)
            .withPayload(payload)
            .withInvocationType(InvocationType.Event); // 비동기 호출

        InvokeResult invokeResult = awsLambda.invoke(invokeRequest);
        // 결과 처리 로직은 필요없음 (비동기 호출이므로 즉시 반환)

        // TODO 아니면 Lambda 함수 자체의 함수 URL로 http request 보내는 방식도 있는듯?
    }
}
