package com.githubsalt.omoib.aws.sqs;

import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SqsMessageListener {

    @SqsListener("omoib-lambda-queue")
    public void handleLambdaResult(String message) {
        // SQS 메시지 처리 로직
        log.info("SQS Received message: " + message);
    }
}
