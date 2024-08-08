package com.githubsalt.omoib.history;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/history")
@Slf4j
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    // TODO: JWT 토큰을 이용한 사용자 인증 처리 구현

    @GetMapping("/{historyId}")
    public ResponseEntity<History> getHistory(@PathVariable Long historyId) {
        History history;
        try {
            history = historyService.findHistory(historyId);
        } catch (IllegalArgumentException e) {
            log.error("History 조회 중 오류가 발생했습니다: ", e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(history);
    }

    @GetMapping("/users/{userId}/histories")
    public ResponseEntity<List<History>> getHistories(@PathVariable Long userId) {
        List<History> histories;
        try {
            histories = historyService.findHistories(userId);
        } catch (IllegalArgumentException e) {
            log.error("History 조회 중 오류가 발생했습니다: ", e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(histories);
    }

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
