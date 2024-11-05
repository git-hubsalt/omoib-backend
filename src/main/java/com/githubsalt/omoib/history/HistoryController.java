package com.githubsalt.omoib.history;

import com.githubsalt.omoib.history.dto.HistoryReviewDTO;
import com.githubsalt.omoib.review.Review;
import com.githubsalt.omoib.review.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/history")
@Slf4j
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;
    private final ReviewService reviewService;

    // TODO: JWT 토큰을 이용한 사용자 인증 처리 구현

    /**
     * 특정 History 조회
     * @param historyId
     * @return
     */
    @GetMapping("/{historyId}")
    public ResponseEntity<HistoryReviewDTO> getHistory(@PathVariable Long historyId) {
        History history;
        Review review;
        try {
            history = historyService.findHistory(historyId);
            review = reviewService.findReview(historyId).orElseThrow(() -> new IllegalArgumentException("History에 연결된 Review가 존재하지 않습니다."));
        } catch (IllegalArgumentException e) {
            log.error("History 조회 중 오류가 발생했습니다: ", e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(HistoryReviewDTO.build(history, review));
    }

    /**
     * 사용자의 특정 타입의 전체 History 조회
     * @param userId
     * @param historyType
     * @return
     */
    @GetMapping("/users/{userId}/histories")
    public ResponseEntity<List<HistoryReviewDTO>> getHistories(@PathVariable Long userId, @RequestParam(name = "historyType") HistoryType historyType) {
        List<History> histories;
        List<HistoryReviewDTO> historyReviewDTOs = new ArrayList<>();
        try {
            histories = historyService.findHistories(userId, historyType);
            for (History history : histories) {
                Review review = reviewService.findReview(history.getId()).orElseThrow(() -> new IllegalArgumentException("History id {%d} 에 연결된 Review가 존재하지 않습니다.".formatted(history.getId())));
                historyReviewDTOs.add(HistoryReviewDTO.build(history, review));
            }
        } catch (IllegalArgumentException e) {
            log.error("History 조회 중 오류가 발생했습니다: ", e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(historyReviewDTOs);
    }

    /**
     * History 생성
     * @param historyId
     * @return
     */
    @DeleteMapping("/{historyId}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long historyId) {
        try {
            historyService.deleteHistory(historyId);
        } catch (IllegalArgumentException e) {
            log.error("History 삭제 중 오류가 발생했습니다: ", e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }

}
