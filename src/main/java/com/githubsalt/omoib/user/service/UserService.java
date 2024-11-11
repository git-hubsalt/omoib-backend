package com.githubsalt.omoib.user.service;

import com.githubsalt.omoib.aws.s3.PresignedURLBuilder;
import com.githubsalt.omoib.closet.dto.SignupRequestDTO;
import com.githubsalt.omoib.global.service.AmazonS3Service;
import com.githubsalt.omoib.user.domain.User;
import com.githubsalt.omoib.user.dto.GetMypageResponseDTO;
import com.githubsalt.omoib.user.dto.UpdateMypageResponseDTO;
import com.githubsalt.omoib.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AmazonS3Service amazonS3Service;
    private final PresignedURLBuilder presignedURLBuilder;

    public Optional<User> findUser(Long userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    public void signup(Long userId, SignupRequestDTO requestDTO, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        String imagePath = amazonS3Service.uploadRow(image, userId);

        user.updateUser(requestDTO.username(), imagePath, null);
    }

    @Transactional(readOnly = true)
    public GetMypageResponseDTO getMypage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        return GetMypageResponseDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .rowImagePath(presignedURLBuilder.buildGetPresignedURL(user.getRowImagePath()).toString())
                .profileImagePath(presignedURLBuilder.buildGetPresignedURL(user.getProfileImagePath()).toString())
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

        String rowImagePath = null;
        String profileImagePath = null;
        if (rowImage != null) {
            rowImagePath = amazonS3Service.uploadRow(rowImage, userId);
        }
        if (profileImage != null) {
            profileImagePath = amazonS3Service.uploadProfile(profileImage, userId);
        }
        user.updateUser(requestDTO.name(), rowImagePath, profileImagePath);
    }
}
