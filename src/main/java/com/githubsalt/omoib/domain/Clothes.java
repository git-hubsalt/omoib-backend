package com.githubsalt.omoib.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Clothes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 옷 이름

    @ManyToMany(mappedBy = "clothesList")
    private List<History> historyList; // 이 옷을 추천받은 History 목록

}
