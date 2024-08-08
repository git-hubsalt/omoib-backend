package com.githubsalt.omoib.repository;

import com.githubsalt.omoib.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
