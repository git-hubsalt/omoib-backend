package com.githubsalt.omoib.history;

import com.githubsalt.omoib.aws.s3.PresignedURLBuilder;
import com.githubsalt.omoib.clothes.domain.Clothes;
import com.githubsalt.omoib.codyrecommendation.dto.RecommendationResultDTO;
import com.githubsalt.omoib.history.dto.HistoryClothesDTO;
import com.githubsalt.omoib.history.enums.HistoryStatus;
import com.githubsalt.omoib.notification.NotifyStatus;
import com.githubsalt.omoib.user.domain.User;
import com.githubsalt.omoib.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {

    private final UserService userService;
    private final HistoryRepository historyRepository;
    private final PresignedURLBuilder presignedURLBuilder;

    /**
     * 사용자의 추천 기록을 생성합니다.
     *
     * @param resultDTO
     * @return
     */
    /*unused*/
    @Transactional
    public Long createHistory(RecommendationResultDTO resultDTO) {
        Long userId = resultDTO.userId();
        List<Clothes> clothesList = resultDTO.clothesList();
        // 사용자가 존재하는지 확인
        User user = findUser(userId);

        // History 생성
        History history = History.builder()
                .type(HistoryType.RECOMMENDATION)
                .date(LocalDateTime.now())
                .user(user)
                .clothesList(clothesList)
                .status(HistoryStatus.COMPLETED)
                .notifyStatus(NotifyStatus.NOT_YET)
                .build();
        historyRepository.save(history);

        return history.getId();
    }

    @Transactional
    public String createPendingHistory(Long userId, HistoryType historyType, List<String> filterTagList) {
        User user = findUser(userId);
        String tagString = String.join(",", filterTagList);
        // History 생성
        LocalDateTime now = LocalDateTime.now();
        History history = History.builder()
                .type(historyType)
                .date(now)
                .user(user)
                .clothesList(null)
                .status(HistoryStatus.PENDING)
                .notifyStatus(NotifyStatus.NOT_YET)
                .filterTagsString(tagString)
                .build();
        historyRepository.save(history);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd-HHmmss");

        return now.format(formatter);
    }

    @Transactional
    public String createPendingFittingHistory(Long userId, List<Clothes> clothesList) {
        User user = findUser(userId);
        // History 생성
        LocalDateTime now = LocalDateTime.now();
        History history = History.builder()
                .type(HistoryType.FITTING)
                .date(now)
                .user(user)
                .clothesList(clothesList)
                .status(HistoryStatus.PENDING)
                .notifyStatus(NotifyStatus.NOT_YET)
                .filterTagsString("")
                .build();
        historyRepository.save(history);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd-HHmmss");

        return now.format(formatter);
    }


    /**
     * 사용자의 추천 기록을 조회합니다. 해당 작업은 조회 후 알림 상태를 읽음으로 변경합니다.
     *
     * @param userId
     * @param historyType
     * @return
     */
    @Transactional
    public List<History> findHistories(Long userId, HistoryType historyType) {
        List<History> byUserAndType = historyRepository.findByUserAndType(findUser(userId), historyType);
        for (History history : byUserAndType) {
            if (history.getNotifyStatus().equals(NotifyStatus.NOT_YET)) {
                history.setNotifyStatus(NotifyStatus.NOTIFIED);
                historyRepository.save(history);
            }
        }
        return byUserAndType;
    }

    @Transactional(readOnly = true)
    public History findPendingHistory(Long userId, HistoryType historyType) {
        return historyRepository.findByUserIdAndStatusAndType(userId, HistoryStatus.PENDING, historyType).orElse(null);
    }

    /**
     * 특정 추천 기록을 조회합니다. 해당 작업은 조회 후 알림 상태를 읽음으로 변경합니다.
     *
     * @param historyId
     * @return
     */
    @Transactional
    public History findHistory(Long historyId) {
        History history = historyRepository.findById(historyId).orElseThrow(() -> new IllegalArgumentException("추천 기록을 찾을 수 없습니다."));
        if (history.getNotifyStatus().equals(NotifyStatus.NOT_YET)) {
            history.setNotifyStatus(NotifyStatus.NOTIFIED);
            historyRepository.save(history);
        }
        history.setFittingImageURL(presignedURLBuilder.buildGetPresignedURL(history.getFittingImageURL()).toString());
        return history;
    }

    /**
     * 특정 사용자가 아직 알림받지 못한 추천 기록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return
     */
    @Transactional(readOnly = true)
    public List<History> findUnNotifiedHistory(Long userId) {
        return historyRepository.findAllByUserIdAndNotifyStatus(userId, NotifyStatus.NOT_YET);
    }

    /**
     * 특정 추천 기록을 삭제합니다.
     *
     * @param historyId
     */
    @Transactional
    public void deleteHistory(Long historyId) {
        historyRepository.deleteById(historyId);
    }

    /**
     * 특정 추천 기록의 소유자인지 확인합니다.
     *
     * @param userId
     * @param historyId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isHistoryOwner(Long userId, Long historyId) {
        return historyRepository.findById(historyId).orElseThrow().getUser().getId().equals(userId);
    }


    private User findUser(Long userId) {
        return userService.findUser(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<HistoryClothesDTO> getHistoryClothes(Long userId, HistoryType historyType) {
        // userId로 모든 history를 찾아서 clothesIdList로 변환
        List<History> histories = findHistories(userId, historyType);
        List<HistoryClothesDTO> historyClothesDTOList = new ArrayList<>();
        for (History history : histories) {
            List<Long> clothesIdList = new ArrayList<>();
            for (Clothes clothes : history.getClothesList()) {
                clothesIdList.add(clothes.getId());
            }
            historyClothesDTOList.add(new HistoryClothesDTO(clothesIdList));
        }
        return historyClothesDTOList;
    }

    @Transactional(readOnly = true)
    public boolean hasPendingHistory(Long userId) {
        return historyRepository.existsByUserIdAndStatus(userId, HistoryStatus.PENDING);
    }

    @Transactional
    public void updateHistory(History history) {
        historyRepository.save(history);
    }
}
