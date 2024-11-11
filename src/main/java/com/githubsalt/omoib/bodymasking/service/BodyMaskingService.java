package com.githubsalt.omoib.bodymasking.service;

import com.githubsalt.omoib.aws.lambda.LambdaController;
import com.githubsalt.omoib.aws.s3.PresignedURLBuilder;
import com.githubsalt.omoib.bodymasking.domain.BodyMasking;
import com.githubsalt.omoib.bodymasking.dto.MaskingLambdaRequestDTO;
import com.githubsalt.omoib.bodymasking.enums.MaskingType;
import com.githubsalt.omoib.bodymasking.repository.BodyMaskingRepository;
import com.githubsalt.omoib.global.service.AmazonS3Service;
import com.githubsalt.omoib.user.domain.User;
import com.githubsalt.omoib.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class BodyMaskingService {

    private final UserService userService;
    private final BodyMaskingRepository bodyMaskingRepository;
    private final LambdaController lambdaController;
    private final AmazonS3Service amazonS3Service;
    private final PresignedURLBuilder presignedURLBuilder;

    @Value("${aws.lambda.masking-name}")
    private String MASKING_LAMBDA_NAME;

    public void addBodyMaskingImage(Long userId, MultipartFile image) {
        userService.findUser(userId).orElseGet(() -> {
            log.error("User not found. userId: {}", userId);
            throw new IllegalArgumentException("User not found");
        });

        String key = amazonS3Service.uploadRow(image, userId);
        URL presignedURL = presignedURLBuilder.buildGetPresignedURL(key);

        lambdaController.invoke(MASKING_LAMBDA_NAME, new MaskingLambdaRequestDTO(userId.toString(), presignedURL.toString()));
    }

    public void process(String userId, String timestamp) {
        User user = userService.findUser(Long.parseLong(userId)).orElseThrow();
        for (MaskingType maskingType : MaskingType.values()) {
            String key = String.format("users/%s/masking/%s/%s.jpg", userId, timestamp, maskingType.name().toLowerCase());
            bodyMaskingRepository.save(BodyMasking.builder().
                    user(user).
                    maskingType(maskingType).
                    imagePath(key).
                    createAt(LocalDateTime.parse(timestamp)).
                    build());
        }
    }

    public String getBodyMaskingImage(Long userId, MaskingType maskingType) {
        return bodyMaskingRepository.findByUserAndMaskingType(
                        userService.findUser(userId).orElseThrow(), maskingType).orElseThrow().
                getImagePath();
    }
}
