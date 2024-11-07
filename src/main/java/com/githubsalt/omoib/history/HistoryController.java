package com.githubsalt.omoib.history;

import com.githubsalt.omoib.history.dto.HistoryBriefListDTO;
import com.githubsalt.omoib.history.dto.HistoryResponseDTO;
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

    /**
     * 특정 History 조회
     * @param historyId
     * @return
     */
    @GetMapping("/{historyId}")
    public ResponseEntity<HistoryResponseDTO> getHistory(@PathVariable Long historyId) {
        HistoryResponseDTO history;
        try {
            history = HistoryResponseDTO.of(historyService.findHistory(historyId));
        } catch (IllegalArgumentException e) {
            log.error("History 조회 중 오류가 발생했습니다: ", e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(history);
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
