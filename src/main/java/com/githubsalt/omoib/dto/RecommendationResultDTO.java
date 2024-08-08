package com.githubsalt.omoib.dto;

import com.githubsalt.omoib.domain.Clothes;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RecommendationResultDTO {

    private long userId; // 사용자 ID
    private List<Clothes> clothesList; // 추천된 옷 ID 목록

}
