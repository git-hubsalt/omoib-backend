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

    @Column(name = "row_image_path")
    private String rowImagePath;

    @Builder
    public User(String socialId) {
        this.socialId = socialId;
    }

    public void updateUser(String name, String rowImagePath) {
        if (name != null) {
            this.name = name;
        }
        if (rowImagePath != null) {
            this.rowImagePath = rowImagePath;
        }
    }
}
