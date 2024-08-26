package com.githubsalt.omoib.bodymasking.service;

import com.githubsalt.omoib.bodymasking.domain.BodyMasking;
import com.githubsalt.omoib.bodymasking.enums.MaskingType;
import com.githubsalt.omoib.bodymasking.repository.BodyMaskingRepository;
import com.githubsalt.omoib.user.domain.User;
import com.githubsalt.omoib.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class BodyMaskingService {

    private final UserService userService;
    private final BodyMaskingRepository bodyMaskingRepository;

    public void addBodyMaskingImage(Long userId, MultipartFile image) {
        User user = userService.findUser(userId).orElseGet(() -> {
            log.error("User not found. userId: {}", userId);
            throw new IllegalArgumentException("User not found");
        });

        Map<MaskingType, String> presignedURL = exchange(image);

        assert presignedURL != null;
        LocalDateTime now = LocalDateTime.now();
        for (Map.Entry<MaskingType, String> entry : presignedURL.entrySet()) {
            bodyMaskingRepository.save(BodyMasking.builder().
                    user(user).
                    maskingType(entry.getKey()).
                    imagePath(entry.getValue()).
                    createAt(now).
                    build());
        }
    }

    public String getBodyMaskingImage(Long userId, MaskingType maskingType) {
        return bodyMaskingRepository.findByUserAndMaskingType(
                        userService.findUser(userId).orElseThrow(), maskingType).orElseThrow().
                getImagePath();
    }

    private Map<MaskingType, String> exchange(MultipartFile image) {
        // TODO
        // return {MaskingType.SHIRT: "https://s3.presigned.url",
        //        MaskingType.OUTER: "https://s3.presigned.url",
        //        MaskingType.PANTS: "https://s3.presigned.url",
        //        MaskingType.SKIRT: "https://s3.presigned.url",
        //        MaskingType.DRESS: "https://s3.presigned.url"};
        return null;
    }
}
