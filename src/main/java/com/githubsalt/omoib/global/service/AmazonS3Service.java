package com.githubsalt.omoib.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.githubsalt.omoib.aws.s3.PresignedURLBuilder;
import com.githubsalt.omoib.global.enums.ClothesStorageType;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket.name}")
    private String bucketName;
    private final RestTemplate restTemplate;
    private final PresignedURLBuilder presignedURLBuilder;

    public String uploadClothes(
            MultipartFile file,
            long userId,
            ClothesStorageType clothesStorageType,
            String clothesName
    ) {
        String originalFilename = file.getOriginalFilename();
        String fileName = clothesName;
        if (originalFilename != null && originalFilename.contains(".")) {
            fileName = fileName + originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String key = String.format(
                "users/%s/items/%s/%s", userId, clothesStorageType.name().toLowerCase(), fileName
        );
        return upload(file, key);
    }

    public String uploadProfile(MultipartFile file, long userId) {
        String key = String.format(
                "users/%s/profile/result.jpg", userId
        );
        return upload(file, key);
    }

    public String uploadRow(MultipartFile file, long userId) {
        String key = String.format(
                "users/%s/row/%s/row.jpg", userId, getS3DateTime()
        );
        return upload(file, key);
    }

    private String upload(MultipartFile uploadFile, String key) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(uploadFile.getContentType());

        URL url = presignedURLBuilder.buildPutPresignedURL(key);
        uploadFileToS3PresignedUrl(url, uploadFile);
        return key;
    }

    private String getS3DateTime() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        return now.format(formatter);
    }

    private void uploadFileToS3PresignedUrl(URL presignedUrl, MultipartFile file) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);

            restTemplate.exchange(presignedUrl.toURI(), HttpMethod.PUT, entity, String.class);
        } catch (IOException e) {
            System.err.println("Failed to read file content: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error uploading file to S3: " + e.getMessage());
        }
    }

    private String getUrlPath(URL url) {
        return url.getProtocol() + "://" + url.getHost() + url.getPath();
    }
}
