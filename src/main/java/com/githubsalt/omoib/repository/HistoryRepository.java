package com.githubsalt.omoib.repository;

import com.githubsalt.omoib.domain.History;
import com.githubsalt.omoib.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByUser(User user);
}
