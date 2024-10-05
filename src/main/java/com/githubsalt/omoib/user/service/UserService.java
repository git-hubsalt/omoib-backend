package com.githubsalt.omoib.user.service;

import com.githubsalt.omoib.closet.dto.SignupRequestDTO;
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

    public Optional<User> findUser(Long userId) {
        return userRepository.findById(userId);
    }

    public void signup(Long userId, SignupRequestDTO requestDTO, MultipartFile image) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("user not found"));

        String imagePath = "";  //TODO s3 save image
        user.updateUser(requestDTO.username(), imagePath);
    }
}
