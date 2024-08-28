package com.githubsalt.omoib.aws.sqs;

import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Service
public class SqsMessageListener {

    @SqsListener("omoib-lambda-queue")
    public void handleLambdaResult(String message) {
        // SQS 메시지 처리 로직
        System.out.println("Received message: " + message);
    }
}
