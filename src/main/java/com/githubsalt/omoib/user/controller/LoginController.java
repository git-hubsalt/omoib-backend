package com.githubsalt.omoib.user.controller;

import com.githubsalt.omoib.user.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "카카오 로그인 redirect uri",
        description = "카카오 로그인 후 인증 결과를 받아 처리하기 위한 uri로 프론트에서 호출 x")
    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<Void> kakaoLoginCallback(@RequestParam String code) {
        return ResponseEntity.ok().build();
    }

}
