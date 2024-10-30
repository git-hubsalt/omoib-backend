package com.githubsalt.omoib.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket.name}")
    private String bucketName;

    public String upload(MultipartFile uploadFile, String key) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(uploadFile.getContentType());

        try {
            this.amazonS3.putObject(bucketName, key, uploadFile.getInputStream(), objectMetadata);
            return amazonS3.getUrl(bucketName, key).toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    public void remove(String key) {
        this.amazonS3.deleteObject(bucketName, key);
    }
}
