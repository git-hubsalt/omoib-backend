package com.githubsalt.omoib.history;

import com.githubsalt.omoib.clothes.domain.Clothes;
import com.githubsalt.omoib.history.enums.HistoryStatus;
import com.githubsalt.omoib.notification.NotifyStatus;
import com.githubsalt.omoib.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class History {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private HistoryType type; // 추천 타입

    private LocalDateTime date; // 추천 받은 일자

    @ManyToOne
    private User user; // 추천 받은 사용자

    @ManyToMany
    @JoinTable(
            name = "history_clothes",
            joinColumns = @JoinColumn(name = "history_id"),
            inverseJoinColumns = @JoinColumn(name = "clothes_id")
    )
    private List<Clothes> clothesList; // 추천 받은 옷들

    @Enumerated(EnumType.STRING)
    private HistoryStatus status; // 추천 상태

    @Enumerated(EnumType.STRING)
    private NotifyStatus notifyStatus; // 알림 상태

    private String fittingImageURL; // 착용한 옷들의 이미지 URL; 추천 타입이 FITTING일 때만 사용

    private String filterTagsString;

}
