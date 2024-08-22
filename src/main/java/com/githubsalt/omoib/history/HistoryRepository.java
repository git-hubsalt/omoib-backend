package com.githubsalt.omoib.history;

import com.githubsalt.omoib.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByUserAndType(User user, HistoryType type);
}
