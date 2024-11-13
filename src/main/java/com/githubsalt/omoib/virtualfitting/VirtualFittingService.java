package com.githubsalt.omoib.virtualfitting;

import com.githubsalt.omoib.aws.dto.SqsFittingResponseMessageDTO;
import com.githubsalt.omoib.clothes.domain.Clothes;
import com.githubsalt.omoib.clothes.dto.BriefClothesDTO;
import com.githubsalt.omoib.clothes.service.ClothesService;
import com.githubsalt.omoib.history.History;
import com.githubsalt.omoib.history.HistoryService;
import com.githubsalt.omoib.history.HistoryType;
import com.githubsalt.omoib.history.enums.HistoryStatus;
import com.githubsalt.omoib.virtualfitting.dto.FittingAIRequestDTO;
import com.githubsalt.omoib.virtualfitting.dto.FittingRequestDTO;
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

    public void fitting(Long userId, FittingRequestDTO requestDTO) {

        if (historyService.hasPendingHistory(userId)) {
            throw new IllegalStateException("이미 피팅 요청이 진행 중입니다.");
        }

        List<BriefClothesDTO> briefClothes = requestDTO.clothes();
        List<Clothes> clothesList = new ArrayList<>();
        for (BriefClothesDTO briefClothesDTO : briefClothes) {
            Clothes clothesItem = clothesService.getClothes(briefClothesDTO.id());
            clothesList.add(clothesItem);
            if (clothesItem == null) {
                throw new IllegalArgumentException("존재하지 않는 옷입니다.");
            }
        }

        String timestamp = historyService.createPendingFittingHistory(userId, clothesList);

        FittingAIRequestDTO aiRequestDTO = new FittingAIRequestDTO(
                userId,
                timestamp,
                requestDTO.clothes()
        );

        // TODO 피팅 모델 endpoint 호출
    }

    public void response(SqsFittingResponseMessageDTO message) {
        History pendingHistory = historyService.findPendingHistory(message.userId(), HistoryType.FITTING);
        pendingHistory.setFittingImageURL(message.imageURL());
        pendingHistory.setStatus(HistoryStatus.COMPLETED);
        historyService.updateHistory(pendingHistory);
    }
}
