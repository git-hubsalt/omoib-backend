package com.githubsalt.omoib.user.controller;

import com.githubsalt.omoib.closet.dto.SignupRequestDTO;
import com.githubsalt.omoib.global.config.security.JwtProvider;
import com.githubsalt.omoib.user.dto.GetMypageResponseDTO;
import com.githubsalt.omoib.user.dto.UpdateMypageResponseDTO;
import com.githubsalt.omoib.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Operation(summary = "카카오 로그인 redirect uri",
        description = "카카오 로그인 후 인증 결과를 받아 처리하기 위한 uri로 프론트에서 호출 x")
    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<Void> kakaoLoginCallback(@RequestParam String code) {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원가입",
        description = "사용자 정보를 입력하고 회원가입을 완료합니다.")
    @PostMapping(path = "/signup", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> signup(
        HttpServletRequest httpServletRequest,
        @RequestPart SignupRequestDTO requestDTO,
        @RequestPart MultipartFile image
    ) {
        Long userId = jwtProvider.getUserId(httpServletRequest);
        userService.signup(userId, requestDTO, image);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "마이페이지 조회",
            description = "마이페이지를 조회합니다.")
    @GetMapping("/mypage")
    public ResponseEntity<GetMypageResponseDTO> getMypage(
            HttpServletRequest httpServletRequest
    ) {
        Long userId = jwtProvider.getUserId(httpServletRequest);
        return ResponseEntity.ok(userService.getMypage(userId));
    }

    @Operation(summary = "마이페이지 수정",
            description = "마이페이지를 수정합니다.")
    @PutMapping(path = "/mypage", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<GetMypageResponseDTO> updateMypage(
            HttpServletRequest httpServletRequest,
            @RequestPart UpdateMypageResponseDTO requestDTO,
            @RequestPart(required = false) MultipartFile rowImage,
            @RequestPart(required = false) MultipartFile profileImage
    ) {
        Long userId = jwtProvider.getUserId(httpServletRequest);
        userService.updateMypage(userId, requestDTO, rowImage, profileImage);
        return ResponseEntity.ok().build();
    }

}
