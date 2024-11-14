package com.githubsalt.omoib.aws.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.net.URL;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PresignedURLBuilder {

    private final String accessKey;
    private final String secretKey;
    private final String bucketName;
    private final AmazonS3 amazonS3;

    public PresignedURLBuilder(@Value("${cloud.aws.credentials.access-key}") String accessKey,
                               @Value("${cloud.aws.credentials.secret-key}") String secretKey,
                               @Value("${cloud.aws.s3.bucket.name}") String bucketName,
                               AmazonS3 amazonS3) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucketName = bucketName;
        this.amazonS3 = amazonS3;
    }


    public URL buildPutPresignedURL(String filePath) {
        return buildPresignedURL(filePath, HttpMethod.PUT);
    }

    public URL buildGetPresignedURL(String filePath) {
        return buildPresignedURL(filePath, HttpMethod.GET);
    }

    private URL buildPresignedURL(String filePath, HttpMethod method) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, filePath)
                .withMethod(method)
                .withExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)); // 1 min
        log.info("Generating Presigned URL for: {}", filePath);

        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        log.info("Generated Presigned URL: {}", url);
        return url;
    }

}
