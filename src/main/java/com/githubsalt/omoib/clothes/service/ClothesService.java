package com.githubsalt.omoib.clothes.service;

import com.githubsalt.omoib.clothes.domain.Clothes;
import com.githubsalt.omoib.clothes.dto.BriefClothesDTO;
import com.githubsalt.omoib.clothes.dto.GetClothesResponseDTO;
import com.githubsalt.omoib.clothes.dto.RegisterClothesRequestDTO;
import com.githubsalt.omoib.clothes.dto.UpdateClothesRequestDTO;
import com.githubsalt.omoib.clothes.enums.ClothesType;
import com.githubsalt.omoib.clothes.repository.ClothesRepository;
import com.githubsalt.omoib.global.enums.ClothesStorageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClothesService {

    private final ClothesRepository clothesRepository;

    @Transactional(readOnly = true)
    public GetClothesResponseDTO getClothesList(ClothesStorageType clothesStorageType) {
        Map<ClothesType, ArrayList<GetClothesResponseDTO.ClothesItemDTO>> typeArrayListMap = Map.of(
                ClothesType.upper, new ArrayList<>(),
                ClothesType.lower, new ArrayList<>(),
                ClothesType.shoes, new ArrayList<>(),
                ClothesType.bag, new ArrayList<>(),
                ClothesType.cap, new ArrayList<>(),
                ClothesType.outer, new ArrayList<>(),
                ClothesType.overall, new ArrayList<>()
        );
        List<Clothes> clothes = clothesRepository.findAllByClothesStorageType(clothesStorageType);
        for (Clothes cloth : clothes) {
            ArrayList<String> tagList = new ArrayList<>();
            tagList.add(cloth.getSeasonType().name());
            GetClothesResponseDTO.ClothesItemDTO clothesItemDTO = new GetClothesResponseDTO.ClothesItemDTO(
                cloth.getId(),
                cloth.getName(),
                cloth.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
                tagList,
                cloth.getImagePath()
            );

            typeArrayListMap.get(cloth.getClothesType()).add(clothesItemDTO);
        }
        return new GetClothesResponseDTO(
            typeArrayListMap.get(ClothesType.upper),
            typeArrayListMap.get(ClothesType.lower),
            typeArrayListMap.get(ClothesType.shoes),
            typeArrayListMap.get(ClothesType.bag),
            typeArrayListMap.get(ClothesType.cap),
            typeArrayListMap.get(ClothesType.outer),
            typeArrayListMap.get(ClothesType.overall)
        );
    }

    @Transactional
    public void registerClothes(
        RegisterClothesRequestDTO requestDTO,
        MultipartFile image,
        ClothesStorageType clothesStorageType
    ) {
        String imagePath = "";  //TODO s3 save image
        Clothes clothes = clothesRepository.save(
            Clothes.builder()
                .name(requestDTO.name())
                .clothesType(requestDTO.clothesType())
                .seasonType(requestDTO.seasonType())
                .imagePath(imagePath)
                .clothesStorageType(clothesStorageType)
                .build()
        );
        //TODO 벡터 lambda
    }

    @Transactional
    public void updateClothes(
        Long userId,
        Long clothesId,
        UpdateClothesRequestDTO requestDTO,
        MultipartFile image
    ) {
        Clothes clothes = clothesRepository.findByIdAndUserId(clothesId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Clothes not found"));
        if (image != null) {
            String imagePath = "";  //TODO s3 save image
            clothes.updateImage(imagePath);
        }
        clothes.update(requestDTO);
    }

    @Transactional
    public void removeClothes(Long userId, Long clothesId) {
        Clothes clothes = clothesRepository.findByIdAndUserId(clothesId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Clothes not found"));
        clothesRepository.delete(clothes);
    }

    public Clothes getClothes(Long clothesId) {
        return clothesRepository.findById(clothesId).orElse(null);
    }

    public BriefClothesDTO getBriefClothes(Long clothesId) {
        //TODO
        return new BriefClothesDTO(1L, "name", null, null, "url");
    }

}
