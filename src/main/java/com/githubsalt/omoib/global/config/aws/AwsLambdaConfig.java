package com.githubsalt.omoib.global.config.aws;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsLambdaConfig {

    @Bean
    public AWSLambda awsLambda() {
        return AWSLambdaClientBuilder.standard()
            .withRegion("ap-northeast-2")
            .build();
    }

    @Bean
    public AmazonSQS amazonSQS() {
        return AmazonSQSClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        "https://sqs.ap-northeast-2.amazonaws.com", "ap-northeast-2"))
                .build();
    }
}
