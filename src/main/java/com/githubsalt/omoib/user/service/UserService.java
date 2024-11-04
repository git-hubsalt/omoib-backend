package com.githubsalt.omoib.user.service;

import com.githubsalt.omoib.closet.dto.SignupRequestDTO;
import com.githubsalt.omoib.global.service.AmazonS3Service;
import com.githubsalt.omoib.global.util.AesEncryptionUtil;
import com.githubsalt.omoib.user.domain.User;
import com.githubsalt.omoib.user.dto.GetMypageResponseDTO;
import com.githubsalt.omoib.user.dto.UpdateMypageResponseDTO;
import com.githubsalt.omoib.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AmazonS3Service amazonS3Service;
    private final AesEncryptionUtil aesEncryptionUtil;

    public Optional<User> findUser(Long userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    public void signup(Long userId, SignupRequestDTO requestDTO, MultipartFile image) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("user not found"));

        String imagePath = uploadImageIfPresent(userId, image);

        user.updateUser(requestDTO.username(), imagePath, null);
    }

    @Transactional(readOnly = true)
    public GetMypageResponseDTO getMypage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        return GetMypageResponseDTO.builder()
                .name(user.getName())
                .email(user.getSocialId())
                .rowImagePath(user.getRowImagePath())
                .profileImagePath(user.getProfileImagePath())
                .build();
    }

    @Transactional
    public void updateMypage(
            Long userId, UpdateMypageResponseDTO requestDTO,
            MultipartFile rowImage,
            MultipartFile profileImage
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        String rowImagePath = uploadImageIfPresent(userId, rowImage);
        String profileImagePath = uploadImageIfPresent(userId, profileImage);

        user.updateUser(requestDTO.name(), rowImagePath, profileImagePath);
    }

    private String uploadImageIfPresent(Long userId, MultipartFile image) {
        if (image == null) {
            return null;
        }
        String s3Key = generateImageS3Key(userId);
        return amazonS3Service.upload(image, s3Key);
    }

    private String generateImageS3Key(Long userId) {
        String name = aesEncryptionUtil.encrypt(userId.toString());
        return String.format("users/%s/items/row/%s", userId, name);
    }
}
