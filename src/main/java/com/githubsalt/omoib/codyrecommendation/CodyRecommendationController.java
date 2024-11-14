package com.githubsalt.omoib.codyrecommendation;

import com.githubsalt.omoib.codyrecommendation.dto.RecommendationRequestDTO;
import com.githubsalt.omoib.codyrecommendation.dto.RecommendationResponseDTO;
import com.githubsalt.omoib.global.config.security.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
 import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cody-recommendation")
@Slf4j
@RequiredArgsConstructor
public class CodyRecommendationController {

    private final CodyRecommendationService codyRecommendationService;
    private final JwtProvider jwtProvider;

    @PostMapping("/recommend")
    public ResponseEntity<RecommendationResponseDTO> recommend(
            HttpServletRequest request,
            @Validated @RequestBody RecommendationRequestDTO requestDTO) {

        log.info("Cody 추천 요청: {}", requestDTO);

        // Cody 추천 로직 엔드포인트 호출
        Long userId = jwtProvider.getUserId(request);
        codyRecommendationService.recommend(userId, requestDTO);

        return ResponseEntity.accepted().build();
    }

}
