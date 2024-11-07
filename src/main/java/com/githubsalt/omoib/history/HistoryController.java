package com.githubsalt.omoib.history;

import com.githubsalt.omoib.history.dto.HistoryBriefListDTO;
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
            review = reviewService.findReview(historyId).orElse(null);
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
    public ResponseEntity<List<HistoryBriefListDTO>> getHistories(@PathVariable Long userId, @RequestParam(name = "historyType") HistoryType historyType) {
        List<HistoryBriefListDTO> histories = new ArrayList<>();
        try {
            List<History> historyList = historyService.findHistories(userId, historyType);
            for (History history : historyList) {
                histories.add(HistoryBriefListDTO.of(history));
            }
        } catch (IllegalArgumentException e) {
            log.error("History 조회 중 오류가 발생했습니다: ", e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(histories);
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
