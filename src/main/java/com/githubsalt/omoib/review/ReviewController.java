package com.githubsalt.omoib.review;

import com.githubsalt.omoib.global.config.security.JwtProvider;
import com.githubsalt.omoib.review.dto.ReviewFormDTO;
import com.githubsalt.omoib.review.dto.ReviewRequestDTO;
import com.githubsalt.omoib.review.dto.ReviewResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtProvider jwtProvider;

    @PutMapping("/update")
    public ResponseEntity<ReviewResponseDTO> updateReview(HttpServletRequest request, @Validated @RequestBody ReviewFormDTO formDTO) {
        log.info("리뷰 처리 요청: {}", formDTO);

        Long userId = jwtProvider.getUserId(request);
        ReviewRequestDTO requestDTO = new ReviewRequestDTO(userId, formDTO.historyId(), formDTO.text());

        // 리뷰 처리 로직
        ReviewResponseDTO reviewResponseDTO = reviewService.processReview(requestDTO);

        return ResponseEntity.ok(reviewResponseDTO);
    }

}
