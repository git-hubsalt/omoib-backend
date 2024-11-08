package com.githubsalt.omoib.codyrecommendation;

import com.githubsalt.omoib.aws.lambda.LambdaService;
import com.githubsalt.omoib.aws.sqs.dto.SqsRecommendResponseMessageDTO;
import com.githubsalt.omoib.clothes.domain.Clothes;
import com.githubsalt.omoib.clothes.dto.BriefClothesDTO;
import com.githubsalt.omoib.clothes.dto.ClothesResponseDTO;
import com.githubsalt.omoib.clothes.service.ClothesService;
import com.githubsalt.omoib.codyrecommendation.dto.RecommendationAIRequestDTO;
import com.githubsalt.omoib.codyrecommendation.dto.RecommendationRequestDTO;
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
    private final LambdaService lambdaService;

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

        List<BriefClothesDTO> briefRequiredClothesList = new ArrayList<>();
        List<BriefClothesDTO> briefCandidateClothesList = new ArrayList<>();
        for (ClothesResponseDTO.ClothesItemDTO clothesItemDTO : allClothesItems) {
            // 필수 옷 처리
            if (requestDTO.requiredClothes().contains(clothesItemDTO.id())) {
                briefRequiredClothesList.add(clothesService.getBriefClothes(clothesItemDTO.id()));
            } else {
                briefCandidateClothesList.add(clothesService.getBriefClothes(clothesItemDTO.id()));
            }
        }

        List<HistoryClothesDTO> exclude = historyService.getHistoryClothes(userId, HistoryType.RECOMMENDATION);

        RecommendationAIRequestDTO aiModelRequestDTO = new RecommendationAIRequestDTO(
                userId,
                timestamp,
                briefRequiredClothesList,
                briefCandidateClothesList, exclude);

        // TODO 추천 모델 endpoint 호출
    }

    public void response(SqsRecommendResponseMessageDTO message) {
        List<Clothes> clothesList = new ArrayList<>();
        for (Long clothesId : message.result()) {
            clothesList.add(clothesService.getClothes(clothesId));
        }
        History pendingHistory = historyService.findPendingHistory(message.userId());
        pendingHistory.setClothesList(clothesList);
        pendingHistory.setStatus(HistoryStatus.COMPLETED);
        historyService.updateHistory(pendingHistory);

        // TODO FE endpoint 호출
    }

}
