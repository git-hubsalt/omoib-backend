package com.githubsalt.omoib.repository;

import com.githubsalt.omoib.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByHistoryId(Long historyId);

}
