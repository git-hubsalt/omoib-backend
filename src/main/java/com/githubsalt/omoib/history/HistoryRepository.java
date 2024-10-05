package com.githubsalt.omoib.history;

import com.githubsalt.omoib.history.enums.HistoryStatus;
import com.githubsalt.omoib.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByUserAndType(User user, HistoryType type);

    boolean existsByUserIdAndStatus(Long userId, HistoryStatus historyStatus);
    Optional<History> findByUserIdAndStatus(Long userId, HistoryStatus historyStatus);
}
