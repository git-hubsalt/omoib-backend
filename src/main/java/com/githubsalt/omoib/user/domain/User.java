package com.githubsalt.omoib.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name; // 사용자 이름

    @Column(name = "social_id")
    private String socialId;

    @Column(name = "email")
    private String email;

    @Column(name = "row_image_path", columnDefinition = "TEXT")
    private String rowImagePath;

    @Column(name = "profile_image_path", columnDefinition = "TEXT")
    private String profileImagePath;

    @Builder
    public User(String socialId, String email) {
        this.socialId = socialId;
        this.email = email;
    }

    private String lastMaskingTimestamp;

    public void updateUser(String name, String rowImagePath, String profileImagePath, String lastMaskingTimestamp) {
        if (name != null) {
            this.name = name;
        }
        if (rowImagePath != null) {
            this.rowImagePath = rowImagePath;
        }
        if (profileImagePath != null) {
            this.profileImagePath = profileImagePath;
        }
        if (lastMaskingTimestamp != null) {
            this.lastMaskingTimestamp = lastMaskingTimestamp;
        }
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
