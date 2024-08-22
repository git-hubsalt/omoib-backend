package com.githubsalt.omoib.history;

import com.githubsalt.omoib.clothes.domain.Clothes;
import com.githubsalt.omoib.codyrecommendation.dto.RecommendationResultDTO;
import com.githubsalt.omoib.user.User;
import com.githubsalt.omoib.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {

    private final UserService userService;
    private final HistoryRepository historyRepository;

    /**
     * 사용자의 추천 기록을 생성합니다.
     * @param resultDTO
     * @return
     */
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
                .build();
        historyRepository.save(history);

        return history.getId();
    }

    /**
     * 사용자의 추천 기록을 조회합니다.
     * @param userId
     * @param historyType
     * @return
     */
    @Transactional(readOnly = true)
    public List<History> findHistories(Long userId, HistoryType historyType) {
        return historyRepository.findByUserAndType(findUser(userId), historyType);
    }

    /**
     * 특정 추천 기록을 조회합니다.
     * @param historyId
     * @return
     */
    @Transactional(readOnly = true)
    public History findHistory(Long historyId) {
        return historyRepository.findById(historyId).orElseThrow(() -> new IllegalArgumentException("추천 기록을 찾을 수 없습니다."));
    }

    /**
     * 특정 추천 기록을 삭제합니다.
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
        return findHistory(historyId).getUser().getId().equals(userId);
    }


    private User findUser(Long userId) {
        return userService.findUser(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
