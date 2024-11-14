package com.githubsalt.omoib.codyrecommendation;

import com.githubsalt.omoib.aws.sqs.dto.SqsRecommendResponseMessageDTO;
import com.githubsalt.omoib.clothes.domain.Clothes;
import com.githubsalt.omoib.clothes.dto.BriefClothesAIDTO;
import com.githubsalt.omoib.clothes.dto.ClothesResponseDTO;
import com.githubsalt.omoib.clothes.service.ClothesService;
import com.githubsalt.omoib.codyrecommendation.dto.RecommendationAIRequestDTO;
import com.githubsalt.omoib.codyrecommendation.dto.RecommendationRequestDTO;
import com.githubsalt.omoib.global.config.aws.SageMakerInvoker;
import com.githubsalt.omoib.history.History;
import com.githubsalt.omoib.history.HistoryService;
import com.githubsalt.omoib.history.HistoryType;
import com.githubsalt.omoib.history.dto.HistoryClothesDTO;
import com.githubsalt.omoib.history.enums.HistoryStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodyRecommendationService {

    private final ClothesService clothesService;
    private final HistoryService historyService;
    private final SageMakerInvoker sageMakerInvoker;

    public void recommend(Long userId, RecommendationRequestDTO requestDTO) {

        if (historyService.hasPendingHistory(userId)) {
            throw new IllegalStateException("이미 추천 요청이 진행 중입니다.");
        }

        String timestamp = historyService.createPendingHistory(userId, HistoryType.RECOMMENDATION, requestDTO.filterTagList());

        // Issue #8: 추천 모델의 경우의 수 과다 문제로 추천 모델에 들어가는 옷의 종류를 제한함.

        ClothesResponseDTO clothesResponseDTO = clothesService.getClothesList(userId);

        List<ClothesResponseDTO.ClothesItemDTO> allClothesItems = new ArrayList<>();
        allClothesItems.addAll(clothesResponseDTO.upper());
        allClothesItems.addAll(clothesResponseDTO.lower());
        allClothesItems.addAll(clothesResponseDTO.overall());

        List<BriefClothesAIDTO> briefRequiredClothesList = new ArrayList<>();
        List<BriefClothesAIDTO> briefCandidateClothesList = new ArrayList<>();
        for (ClothesResponseDTO.ClothesItemDTO clothesItemDTO : allClothesItems) {
            // 필수 옷 처리
            if (requestDTO.requiredClothes().contains(clothesItemDTO.id())) {
                briefRequiredClothesList.add(BriefClothesAIDTO.of(
                        clothesService.getBriefClothes(clothesItemDTO.id())
                ));
            } else {
                briefCandidateClothesList.add(BriefClothesAIDTO.of(
                        clothesService.getBriefClothes(clothesItemDTO.id())
                ));
            }
        }

        List<HistoryClothesDTO> exclude = historyService.getHistoryClothes(userId, HistoryType.RECOMMENDATION);

        RecommendationAIRequestDTO aiModelRequestDTO = new RecommendationAIRequestDTO(
                userId.toString(),
                timestamp,
                briefRequiredClothesList,
                briefCandidateClothesList,
                exclude,
                String.join(",", requestDTO.filterTagList()));

        sageMakerInvoker.invokeEndpoint("oft-12", aiModelRequestDTO);
    }

    public void response(SqsRecommendResponseMessageDTO message) {
        List<Clothes> clothesList = new ArrayList<>();
        clothesList.add(clothesService.getClothes(Long.valueOf((message.prediction().get(0)))));
        clothesList.add(clothesService.getClothes(Long.valueOf((message.prediction().get(1)))));

        History pendingHistory = historyService.findPendingHistory(Long.parseLong(message.userId()), HistoryType.RECOMMENDATION);
        pendingHistory.setClothesList(clothesList);
        pendingHistory.setStatus(HistoryStatus.COMPLETED);
        historyService.updateHistory(pendingHistory);
    }

}
