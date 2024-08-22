package com.githubsalt.omoib.codyrecommendation;

import com.githubsalt.omoib.codyrecommendation.dto.RecommendationRequestDTO;
import com.githubsalt.omoib.codyrecommendation.dto.RecommendationResponseDTO;
import com.githubsalt.omoib.codyrecommendation.dto.RecommendationResultDTO;
import com.githubsalt.omoib.history.HistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cody-recommendation")
@Slf4j
@RequiredArgsConstructor
public class CodyRecommendationController {

    private final CodyRecommendationService codyRecommendationService;
    private final HistoryService historyService;

    @PostMapping("/recommend")
    public ResponseEntity<RecommendationResponseDTO> recommend(@Validated @ModelAttribute RecommendationRequestDTO requestDTO) {
        log.info("Cody 추천 요청: {}", requestDTO);

        // Cody 추천 로직 엔드포인트 호출
        RecommendationResultDTO resultDTO = codyRecommendationService.recommend(requestDTO);

        // Cody 추천 결과를 히스토리로 저장, 히스토리 ID 반환
        Long historyId = historyService.createHistory(resultDTO);
        RecommendationResponseDTO responseDTO = new RecommendationResponseDTO(historyId);

        return ResponseEntity.ok(responseDTO);
    }

}