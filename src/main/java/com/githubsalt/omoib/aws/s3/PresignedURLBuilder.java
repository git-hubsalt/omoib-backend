package com.githubsalt.omoib.aws.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Date;

@Component
@Slf4j
public class PresignedURLBuilder {

    private final String accessKey;
    private final String secretKey;
    private final String bucketName;

    public PresignedURLBuilder(@Value("${cloud.aws.credentials.access-key}") String accessKey,
                               @Value("${cloud.aws.credentials.secret-key}") String secretKey,
                               @Value("${cloud.aws.s3.bucket.name}") String bucketName) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucketName = bucketName;
    }


    public URL buildPresignedURL(String filePath) {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, filePath)
                .withMethod(com.amazonaws.HttpMethod.GET)
                .withExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 1)); // 1 min
        log.info("Generating Presigned URL for: {}", filePath);

        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        log.info("Generated Presigned URL: {}", url);
        return url;
    }

}
