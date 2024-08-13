package com.githubsalt.omoib.review;

import com.githubsalt.omoib.review.dto.ReviewRequestDTO;
import com.githubsalt.omoib.review.dto.ReviewResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PutMapping("/update")
    public ResponseEntity<ReviewResponseDTO> updateReview(@Validated @ModelAttribute ReviewRequestDTO requestDTO) {
        log.info("리뷰 처리 요청: {}", requestDTO);

        // 리뷰 처리 로직
        ReviewResponseDTO reviewResponseDTO = reviewService.processReview(requestDTO);

        return ResponseEntity.ok(reviewResponseDTO);
    }

}
