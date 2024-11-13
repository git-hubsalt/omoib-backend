package com.githubsalt.omoib.virtualfitting;

import com.githubsalt.omoib.aws.s3.PresignedURLBuilder;
import com.githubsalt.omoib.clothes.domain.Clothes;
import com.githubsalt.omoib.clothes.dto.BriefClothesDTO;
import com.githubsalt.omoib.clothes.enums.ClothesType;
import com.githubsalt.omoib.clothes.service.ClothesService;
import com.githubsalt.omoib.history.History;
import com.githubsalt.omoib.history.HistoryService;
import com.githubsalt.omoib.history.HistoryType;
import com.githubsalt.omoib.history.enums.HistoryStatus;
import com.githubsalt.omoib.user.domain.User;
import com.githubsalt.omoib.user.service.UserService;
import com.githubsalt.omoib.virtualfitting.dto.FittingAIRequestDTO;
import com.githubsalt.omoib.virtualfitting.dto.FittingRequestDTO;
import com.githubsalt.omoib.virtualfitting.dto.SqsFittingResponseMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VirtualFittingService {

    private final ClothesService clothesService;
    private final HistoryService historyService;
    private final UserService userService;
    private final PresignedURLBuilder presignedURLBuilder;

    public void fitting(Long userId, FittingRequestDTO requestDTO) {

        if (historyService.hasPendingHistory(userId)) {
            throw new IllegalStateException("이미 피팅 요청이 진행 중입니다.");
        }

        List<BriefClothesDTO> briefClothes = requestDTO.clothes();
        Clothes upper = null, lower = null;
        for (BriefClothesDTO briefClothesDTO : briefClothes) {
            Clothes clothesItem = clothesService.getClothes(briefClothesDTO.id());
            if (clothesItem.getClothesType() == ClothesType.upper && upper == null) {
                upper = clothesItem;
            } else if (clothesItem.getClothesType() == ClothesType.lower && lower == null) {
                lower = clothesItem;
            }

            if (upper != null && lower != null) {
                break;
            }
        }

        if (upper == null || lower == null) {
            throw new IllegalArgumentException("상의와 하의를 모두 선택해주세요.");
        }
        String timestamp = historyService.createPendingFittingHistory(userId, new ArrayList<>(List.of(upper, lower)));

        User user = userService.findUser(userId).orElseThrow();
        FittingAIRequestDTO aiRequestDTO = new FittingAIRequestDTO(
                userId.toString(),
                timestamp,
                presignedURLBuilder.buildGetPresignedURL(user.getRowImagePath()).toString(),
                presignedURLBuilder.buildGetPresignedURL(upper.getImagePath()).toString(),
                presignedURLBuilder.buildGetPresignedURL(lower.getImagePath()).toString(),
                presignedURLBuilder.buildGetPresignedURL(String.format("users/%s/masking/%s/overall.jpg",
                        userId, user.getLastMaskingTimestamp())).toString(),
                "overall");


        // TODO 피팅 모델 endpoint 호출
    }

    public void response(SqsFittingResponseMessageDTO message) {
        History pendingHistory = historyService.findPendingHistory(Long.parseLong(message.userId()), HistoryType.FITTING);
        pendingHistory.setFittingImageURL("/users/" + message.userId() + "/vton_result/" + message.timestamp() + "result.jpg");
        pendingHistory.setStatus(HistoryStatus.COMPLETED);
        historyService.updateHistory(pendingHistory);
    }
}
