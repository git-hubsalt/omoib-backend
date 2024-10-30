package com.githubsalt.omoib.clothes.domain;

import com.githubsalt.omoib.clothes.converter.ClothesTypeConverter;
import com.githubsalt.omoib.clothes.converter.SeasonTypeConverter;
import com.githubsalt.omoib.clothes.dto.UpdateClothesRequestDTO;
import com.githubsalt.omoib.clothes.enums.ClothesType;
import com.githubsalt.omoib.clothes.enums.SeasonType;
import com.githubsalt.omoib.global.enums.ClothesStorageType;
import com.githubsalt.omoib.history.History;
import com.githubsalt.omoib.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "clothes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Clothes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name; // 옷 이름

    @Convert(converter = ClothesTypeConverter.class)
    @Column(name = "clothes_type", nullable = false)
    private ClothesType clothesType;    //옷 카테고리 (예: "상의", "하의", "기타")

    @Convert(converter = SeasonTypeConverter.class)
    @Column(name = "season_type", nullable = false)
    private SeasonType seasonType;      //계절

    @Column(name = "image_path")
    private String imagePath; // S3 Presigned URL

    @Column(name = "clothes_storage_type", nullable = false)
    private ClothesStorageType clothesStorageType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(mappedBy = "clothesList")
    private List<History> historyList; // 이 옷을 추천받은 History 목록

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @Builder
    public Clothes(
        String name,
        ClothesType clothesType,
        SeasonType seasonType,
        String imagePath,
        ClothesStorageType clothesStorageType
    ) {
        this.name = name;
        this.clothesType = clothesType;
        this.seasonType = seasonType;
        this.imagePath = imagePath;
        this.clothesStorageType = clothesStorageType;
    }

    public void update(UpdateClothesRequestDTO requestDTO) {
        this.name = requestDTO.name();
        this.clothesType = requestDTO.clothesType();
        this.seasonType = requestDTO.seasonType();
    }

    public void updateImage(String imagePath) {
        this.imagePath = imagePath;
    }
}
