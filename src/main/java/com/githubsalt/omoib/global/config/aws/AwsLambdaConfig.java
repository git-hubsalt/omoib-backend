package com.githubsalt.omoib.global.config.aws;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
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
}
