package com.githubsalt.omoib.history;

 import com.githubsalt.omoib.aws.s3.PresignedURLBuilder;
 import com.githubsalt.omoib.clothes.domain.Clothes;
 import com.githubsalt.omoib.clothes.dto.ClothesWithPresignedDTO;
 import com.githubsalt.omoib.clothes.service.ClothesService;
 import com.githubsalt.omoib.global.config.security.JwtProvider;
import com.githubsalt.omoib.history.dto.HistoryResponseDTO;
import com.githubsalt.omoib.history.dto.HistoryReviewDTO;
import com.githubsalt.omoib.review.Review;
import com.githubsalt.omoib.review.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final ClothesService clothesService;
    private final ReviewService reviewService;
    private final JwtProvider jwtProvider;
    private final PresignedURLBuilder presignedURLBuilder;

    /**
     * 특정 History 조회
     * @param historyId
     * @return
     */
    @GetMapping("/{historyId}")
    public ResponseEntity<HistoryReviewDTO> getHistory(@PathVariable Long historyId) {
        HistoryResponseDTO historyResponseDTO;
        Review review;
        try {
            History history = historyService.findHistory(historyId);
            historyResponseDTO = getHistoryResponseDTO(history);

            review = reviewService.findReview(historyId).orElse(null);
        } catch (IllegalArgumentException e) {
            log.error("History 조회 중 오류가 발생했습니다: ", e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(HistoryReviewDTO.build(historyResponseDTO, review));
    }

    /**
     * 사용자의 특정 타입의 전체 History 조회
     * @param historyType
     * @return
     */
    @GetMapping("")
    public ResponseEntity<List<HistoryResponseDTO>> getHistories(HttpServletRequest request, @RequestParam(name = "historyType") HistoryType historyType) {
        Long userId = jwtProvider.getUserId(request);

        List<History> histories;
        List<HistoryResponseDTO> historyResponseDTOs = new ArrayList<>();
        try {
            histories = historyService.findHistories(userId, historyType);
            for (History history : histories) {
                historyResponseDTOs.add(getHistoryResponseDTO(history));
            }
        } catch (IllegalArgumentException e) {
            log.error("History 조회 중 오류가 발생했습니다: ", e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(historyResponseDTOs);
    }

    private HistoryResponseDTO getHistoryResponseDTO(History history) {
        HistoryResponseDTO historyResponseDTO;
        List<Clothes> clothesList = history.getClothesList();
        List<ClothesWithPresignedDTO> clothesListWithPresignedURL = getClothesWithPresignedDTOS(clothesList);

        String historyFittingPresigned = null;
        if (history.getType() == HistoryType.FITTING) {
            historyFittingPresigned = presignedURLBuilder.buildGetPresignedURL(history.getFittingImageURL()).toString();
        }

        historyResponseDTO = new HistoryResponseDTO(history.getId(), history.getType(), clothesListWithPresignedURL,
                historyFittingPresigned, history.getFilterTagsString());
        return historyResponseDTO;
    }

    private List<ClothesWithPresignedDTO> getClothesWithPresignedDTOS(List<Clothes> clothesList) {
        List<ClothesWithPresignedDTO> clothesListWithPresignedURL = new ArrayList<>();
        for (Clothes clothes : clothesList) {
            clothesListWithPresignedURL.add(clothesService.withPresignedDTO(clothes));
        }
        return clothesListWithPresignedURL;
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
