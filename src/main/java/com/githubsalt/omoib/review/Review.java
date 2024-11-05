package com.githubsalt.omoib.review;

import com.githubsalt.omoib.history.History;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "history_id") // FK와 PK 역할을 하는 칼럼
    private History history;

    @Column(nullable = false)
    private String temperatureSuitability; // 온도 적절성 (예: "추웠다", "적당했다", "더웠다")

    @Column(nullable = false)
    private String preference; // 개인 선호도 (예: "별로다", "적절했다", "최고였다")

}