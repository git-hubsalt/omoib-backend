package com.githubsalt.omoib.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    private Long id;

    private String name; // 사용자 이름

    private String socialId;

    @Builder
    public User(Long id, String socialId) {
        this.id = id;
        this.socialId = socialId;
    }
}
