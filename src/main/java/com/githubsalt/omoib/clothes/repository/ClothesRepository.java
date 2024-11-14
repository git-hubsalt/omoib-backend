package com.githubsalt.omoib.clothes.repository;

import com.githubsalt.omoib.clothes.domain.Clothes;
import com.githubsalt.omoib.global.enums.ClothesStorageType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClothesRepository extends JpaRepository<Clothes, Long> {

    Optional<Clothes> findByIdAndUserId(Long clothesId, Long userId);

    List<Clothes> findAllByUserId(Long userId);

    List<Clothes> findAllByClothesStorageTypeAndUserId(ClothesStorageType clothesStorageType, Long userId);
    boolean existsByNameAndUserIdAndClothesStorageType(String name, Long userId, ClothesStorageType clothesStorageType);
}
