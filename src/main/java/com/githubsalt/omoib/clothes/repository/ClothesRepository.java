package com.githubsalt.omoib.clothes.repository;

import com.githubsalt.omoib.clothes.domain.Clothes;
import com.githubsalt.omoib.clothes.enums.ClothesType;
import com.githubsalt.omoib.clothes.enums.SeasonType;
import com.githubsalt.omoib.global.enums.ClothesStorageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClothesRepository extends JpaRepository<Clothes, Long> {

    Optional<Clothes> findByIdAndUserId(Long clothesId, Long userId);
    List<Clothes> findAllByClothesStorageType(ClothesStorageType clothesStorageType);
    boolean existsByNameAndClothesTypeAndSeasonType(String name, ClothesType clothesType, SeasonType seasonType);
}
