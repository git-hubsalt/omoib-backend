package com.githubsalt.omoib.service;

import com.githubsalt.omoib.dto.RecommendationRequestDTO;
import com.githubsalt.omoib.dto.RecommendationResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CodyRecommendationService {

    // TODO: Cody 추천 로직 엔드포인트 호출
    public RecommendationResultDTO recommend(RecommendationRequestDTO requestDTO) {
        return new RecommendationResultDTO(1L, null);
    }
}
