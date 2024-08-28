package com.githubsalt.omoib.bodymasking.controller;

import com.githubsalt.omoib.bodymasking.enums.MaskingType;
import com.githubsalt.omoib.bodymasking.service.BodyMaskingService;
import com.githubsalt.omoib.global.dto.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/bodymasking")
@Slf4j
@RequiredArgsConstructor
public class BodyMaskingController {

    private final BodyMaskingService bodyMaskingService;

    @Operation(summary = "바디마스킹 이미지 등록",
        description = "바디마스킹 이미지를 등록합니다.")
    @PostMapping("")
    public ResponseEntity<?> addBodyMaskingImage(@RequestPart MultipartFile image) {
        Long userId = ((CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        bodyMaskingService.addBodyMaskingImage(userId, image);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "바디마스킹 이미지 조회",
        description = "바디마스킹 이미지를 조회합니다.")
    @GetMapping("")
    public ResponseEntity<?> getBodyMaskingImage(@RequestParam MaskingType maskingType) {
        Long userId = ((CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        return ResponseEntity.ok(bodyMaskingService.getBodyMaskingImage(userId, maskingType));
    }

}
