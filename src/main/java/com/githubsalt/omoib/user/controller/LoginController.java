package com.githubsalt.omoib.user.controller;

import com.githubsalt.omoib.user.dto.LoginResponseDTO;
import com.githubsalt.omoib.user.service.LoginService;
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

    /**
     * kakao login callback
     */
    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<LoginResponseDTO> kakaoLoginCallback(@RequestParam String code) {
        return ResponseEntity.ok(loginService.login(code));
    }

}
