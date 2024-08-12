package com.githubsalt.omoib.clothes.domain;

import com.githubsalt.omoib.clothes.converter.ClothesTypeConverter;
import com.githubsalt.omoib.clothes.converter.SeasonTypeConverter;
import com.githubsalt.omoib.clothes.enums.ClothesType;
import com.githubsalt.omoib.clothes.enums.SeasonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Clothes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 옷 이름

    @Convert(converter = ClothesTypeConverter.class)
    private ClothesType clothesType;    //옷 카테고리 (예: "상의", "하의", "기타")

    @Convert(converter = SeasonTypeConverter.class)
    private SeasonType seasonType;      //계절

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

//    @ManyToMany(mappedBy = "clothesList")
//    private List<History> historyList; // 이 옷을 추천받은 History 목록

}
