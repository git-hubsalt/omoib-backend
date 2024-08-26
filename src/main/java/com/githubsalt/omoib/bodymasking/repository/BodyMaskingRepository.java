package com.githubsalt.omoib.bodymasking.repository;

import com.githubsalt.omoib.bodymasking.domain.BodyMasking;
import com.githubsalt.omoib.bodymasking.enums.MaskingType;
import com.githubsalt.omoib.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BodyMaskingRepository extends JpaRepository<BodyMasking, Long> {
    Optional<BodyMasking> findByUserAndMaskingType(User user, MaskingType maskingType);
}
