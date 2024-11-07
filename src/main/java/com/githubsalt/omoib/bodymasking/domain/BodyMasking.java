package com.githubsalt.omoib.bodymasking.domain;

import com.githubsalt.omoib.bodymasking.enums.MaskingType;
import com.githubsalt.omoib.bodymasking.converter.MaskingTypeConverter;
import com.githubsalt.omoib.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BodyMasking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Convert(converter = MaskingTypeConverter.class)
    private MaskingType maskingType;

    @Column(name = "image_path")
    private String imagePath; // S3 Presigned URL

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt; // 서비스에선 YYMMDD-HHMMSS 타입으로 포맷

}
