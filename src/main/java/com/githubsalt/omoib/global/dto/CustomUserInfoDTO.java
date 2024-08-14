package com.githubsalt.omoib.global.dto;

import lombok.Getter;

@Getter
public class CustomUserInfoDTO {
    private Long userId;

    public CustomUserInfoDTO(Long userId) {
        this.userId = userId;
    }
}