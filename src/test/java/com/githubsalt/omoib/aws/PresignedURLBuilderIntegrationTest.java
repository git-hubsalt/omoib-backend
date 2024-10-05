package com.githubsalt.omoib.aws;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.githubsalt.omoib.aws.s3.PresignedURLBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class PresignedURLBuilderIntegrationTest {

    private PresignedURLBuilder presignedURLBuilder;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket.name}")
    private String bucketName;

    @Value("${cloud.aws.s3.test.url}")
    private String testUrl;

    private AmazonS3 s3Client;

    @BeforeEach
    void setUp() {
        // 실제 AWS S3 클라이언트 설정
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();

        // PresignedURLBuilder 인스턴스 생성
        presignedURLBuilder = new PresignedURLBuilder(accessKey, secretKey, bucketName);
    }

    @Test
    void buildPresignedURL_shouldReturnValidURL() throws Exception {
        // Given
        String filePath = testUrl;

        // When
        URL presignedUrl = presignedURLBuilder.buildPresignedURL(filePath);

        // Then
        assertNotNull(presignedUrl);

        // 테스트 URL이 유효한지 확인
        HttpURLConnection connection = (HttpURLConnection) presignedUrl.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        // 200 OK를 기대
        assertEquals(200, responseCode);
    }
}
