package com.githubsalt.omoib.aws.sqs;

import com.githubsalt.omoib.aws.sqs.dto.SqsMaskingResponseMessageDTO;
import com.githubsalt.omoib.aws.sqs.dto.SqsRecommendResponseMessageDTO;
import com.githubsalt.omoib.bodymasking.service.BodyMaskingService;
import com.githubsalt.omoib.codyrecommendation.CodyRecommendationService;
import com.githubsalt.omoib.virtualfitting.VirtualFittingService;
import com.githubsalt.omoib.virtualfitting.dto.SqsFittingResponseMessageDTO;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SqsMessageListener {

    private final BodyMaskingService bodyMaskingService;
    private final CodyRecommendationService codyRecommendationService;
    private final VirtualFittingService virtualFittingService;

    @SqsListener("omoib-lambda-queue")
    public void handleLambdaResult(SqsMaskingResponseMessageDTO message) {
        // SQS 메시지 처리 로직
        log.info("Masking: SQS Received result: " + message);
        bodyMaskingService.process(message.username(), message.initial_timestamp());
    }

    @SqsListener("omoib-recommendation-queue")
    public void handleRecommendationResult(SqsRecommendResponseMessageDTO message) {
        // SQS 메시지 처리 로직
        log.info("Recommendation: SQS Received result: " + message);
        codyRecommendationService.response(message);
    }

    @SqsListener("omoib-vton-queue")
    public void handleFittingResult(SqsFittingResponseMessageDTO message) {
        // SQS 메시지 처리 로직
        log.info("Vton: SQS Received result: " + message);
        virtualFittingService.response(message);
    }
}
