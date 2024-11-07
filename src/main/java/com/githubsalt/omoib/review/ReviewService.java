package com.githubsalt.omoib.review;

import com.githubsalt.omoib.review.dto.ReviewRequestDTO;
import com.githubsalt.omoib.review.dto.ReviewResponseDTO;
import com.githubsalt.omoib.history.HistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final HistoryService historyService;
    private final ReviewRepository reviewRepository;

    /**
     * 리뷰 처리
     * @param requestDTO
     * @return
     */
    @Transactional
    public ReviewResponseDTO processReview(ReviewRequestDTO requestDTO) {
        log.info("리뷰 수정 요청: {}", requestDTO);

        // 사용자 유효성 검증
        if (historyService.isHistoryOwner(requestDTO.userId(), requestDTO.historyId())) {
            throw new IllegalArgumentException("사용자가 히스토리의 소유자가 아닙니다.");
        }

        // 리뷰 있으면 수정, 없으면 생성
        Review review = findReview(requestDTO.historyId())
                .map(value -> updateReview(requestDTO, value))
                .orElseGet(() -> createReview(requestDTO));

        // 리뷰 저장
        reviewRepository.save(review);
        return new ReviewResponseDTO(review.getHistory().getId(), review.getText());
    }

    private Review updateReview(ReviewRequestDTO requestDTO, Review review) {
        review.setText(requestDTO.text());
        return review;
    }

    private Review createReview(ReviewRequestDTO requestDTO) {
        log.info("리뷰 생성: {}", requestDTO);
        return Review.builder()
                .history(historyService.findHistory(requestDTO.historyId()))
                .text(requestDTO.text())
                .build();
    }

    public Optional<Review> findReview(Long historyId) {
        return reviewRepository.findByHistoryId(historyId);
    }

}
