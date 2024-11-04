package com.githubsalt.omoib.user.service;

import com.githubsalt.omoib.closet.dto.SignupRequestDTO;
import com.githubsalt.omoib.global.service.AmazonS3Service;
import com.githubsalt.omoib.global.util.AesEncryptionUtil;
import com.githubsalt.omoib.user.domain.User;
import com.githubsalt.omoib.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public void signup(Long userId, SignupRequestDTO requestDTO, MultipartFile image) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("user not found"));

        String imagePath = amazonS3Service.upload(image, generateImageS3Key(userId));

        user.updateUser(requestDTO.username(), imagePath);
    }

    private String generateImageS3Key(Long userId) {
        String name = aesEncryptionUtil.encrypt(userId.toString());
        return String.format("users/%s/items/row/%s", userId, name);
    }
}
