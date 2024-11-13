package com.githubsalt.omoib;

import com.githubsalt.omoib.clothes.domain.Clothes;
import com.githubsalt.omoib.clothes.enums.ClothesType;
import com.githubsalt.omoib.clothes.enums.SeasonType;
import com.githubsalt.omoib.clothes.repository.ClothesRepository;
import com.githubsalt.omoib.global.enums.ClothesStorageType;
import com.githubsalt.omoib.history.History;
import com.githubsalt.omoib.history.HistoryRepository;
import com.githubsalt.omoib.history.HistoryType;
import com.githubsalt.omoib.history.enums.HistoryStatus;
import com.githubsalt.omoib.notification.NotifyStatus;
import com.githubsalt.omoib.review.Review;
import com.githubsalt.omoib.review.ReviewRepository;
import com.githubsalt.omoib.user.domain.User;
import com.githubsalt.omoib.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DummyDataLoader implements ApplicationRunner {

    private final UserRepository userRepository;
    private final ClothesRepository clothesRepository;
    private final HistoryRepository historyRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Dummy Data Load Start");
        User user = User.builder()
                .socialId("3782737851")
                .email("test@test.test").build();
        user.updateUser("황수민", "users/3/row/20241108-175657/row.jpg", null, null);
        userRepository.save(user);

        log.info("Dummy User Created");

        Clothes lower = clothesRepository.save(
                Clothes.builder()
                        .name("하의")
                        .clothesType(ClothesType.lower)
                        .seasonType(List.of(SeasonType.봄, SeasonType.가을))
                        .clothesStorageType(ClothesStorageType.CLOSET)
                        .user(user)
                        .imagePath("users/a/items/closet/lower.jpg")
                        .build()
        );
        clothesRepository.save(lower);

        Clothes upper = clothesRepository.save(
                Clothes.builder()
                        .name("상의")
                        .clothesType(ClothesType.upper)
                        .seasonType(List.of(SeasonType.여름, SeasonType.가을))
                        .clothesStorageType(ClothesStorageType.CLOSET)
                        .user(user)
                        .imagePath("users/a/items/closet/upper.jpg")
                        .build()
        );
        clothesRepository.save(upper);
        log.info("Dummy Clothes Created");

        History recommend = History.builder()
                .type(HistoryType.RECOMMENDATION)
                .date(LocalDateTime.now().minusMinutes(10))
                .user(user)
                .clothesList(List.of(upper, lower))
                .status(HistoryStatus.COMPLETED)
                .notifyStatus(NotifyStatus.NOTIFIED)
                .filterTagsString("테스트,테스트2,테스트3")
                .build();
        historyRepository.save(recommend);

        History fitting = History.builder()
                .type(HistoryType.FITTING)
                .date(LocalDateTime.now())
                .user(user)
                .clothesList(List.of(upper, lower))
                .status(HistoryStatus.COMPLETED)
                .notifyStatus(NotifyStatus.NOT_YET)
                .fittingImageURL("users/a/vton_result/241108-230001/result.jpg")
                .build();
        historyRepository.save(fitting);
        log.info("Dummy History Created");

        Review reviewRecommend = Review.builder()
                .history(recommend)
                .text("추천 테스트 리뷰 ㅁ나ㅣㅇ릐ㅏㅁㄴ으리ㅏㅡㅁ니ㅏㅡㄴㅇ라ㅡㅁ니")
                .build();
        reviewRepository.save(reviewRecommend);
        log.info("Dummy Review Created");
        log.info("Dummy Data Load End");

    }
}
