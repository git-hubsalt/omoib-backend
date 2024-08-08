package com.githubsalt.omoib.domain;

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

}
