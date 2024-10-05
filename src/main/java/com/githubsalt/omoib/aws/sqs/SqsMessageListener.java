package com.githubsalt.omoib.aws.sqs;

import com.githubsalt.omoib.aws.sqs.dto.SqsMaskingResponseMessageDTO;
import com.githubsalt.omoib.aws.sqs.dto.SqsRecommendResponseMessageDTO;
import com.githubsalt.omoib.codyrecommendation.CodyRecommendationService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SqsMessageListener {

    private final CodyRecommendationService codyRecommendationService;

    @SqsListener("omoib-lambda-queue")
    public void handleLambdaResult(SqsMaskingResponseMessageDTO message) {
        // SQS 메시지 처리 로직
        log.info("SQS Received result: " + message);
    }

    @SqsListener("omoib-recommendation-queue")
    public void handleRecommendationResult(SqsRecommendResponseMessageDTO message) {
        // SQS 메시지 처리 로직
        log.info("Recommendation: SQS Received result: " + message);
        codyRecommendationService.response(message);
    }
}
