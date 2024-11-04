package com.githubsalt.omoib.clothes.service;

import com.githubsalt.omoib.aws.s3.PresignedURLBuilder;
import com.githubsalt.omoib.clothes.domain.Clothes;
import com.githubsalt.omoib.clothes.dto.BriefClothesDTO;
import com.githubsalt.omoib.clothes.dto.GetClothesResponseDTO;
import com.githubsalt.omoib.clothes.dto.RegisterClothesRequestDTO;
import com.githubsalt.omoib.clothes.dto.UpdateClothesRequestDTO;
import com.githubsalt.omoib.clothes.enums.ClothesType;
import com.githubsalt.omoib.clothes.enums.SeasonType;
import com.githubsalt.omoib.clothes.repository.ClothesRepository;
import com.githubsalt.omoib.global.enums.ClothesStorageType;
import com.githubsalt.omoib.global.service.AmazonS3Service;
import com.githubsalt.omoib.global.util.AesEncryptionUtil;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ClothesService {

    private final ClothesRepository clothesRepository;
    private final PresignedURLBuilder presignedURLBuilder;
    private final AmazonS3Service amazonS3Service;
    private final AesEncryptionUtil aesEncryptionUtil;

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
            for (SeasonType seasonType : cloth.getSeasonType()) {
                tagList.add(seasonType.name());
            }
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
        ClothesStorageType clothesStorageType,
        Long userId
    ) {
        for (RegisterClothesRequestDTO.RegisterClothesDTO clothesDTO : requestDTO.clothes()) {
            checkDuplicateClothesName(clothesDTO.name(), userId);
            Clothes clothes = clothesRepository.save(
                    Clothes.builder()
                            .name(clothesDTO.name())
                            .clothesType(clothesDTO.clothesType())
                            .seasonType(clothesDTO.seasonType())
                            .clothesStorageType(clothesStorageType)
                            .build()
            );
            String imagePath = uploadS3Image(image, clothes.getId(), clothesStorageType, userId);
            clothes.updateImage(imagePath);
        }
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
            String imagePath = uploadS3Image(image, clothes.getId(), clothes.getClothesStorageType(), userId);
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
        Clothes clothes = clothesRepository.findById(clothesId).orElseThrow(() -> new IllegalArgumentException("Clothes not found"));
        return new BriefClothesDTO(
                clothes.getId(),
                clothes.getName(),
                clothes.getClothesType(),
                clothes.getSeasonType(),
                presignedURLBuilder.buildPresignedURL(clothes.getImagePath()).toString()
        );
    }

    private String uploadS3Image(
            MultipartFile image,
            Long clothesId,
            ClothesStorageType clothesStorageType,
            Long userId
    ) {
        String key = generateImageS3Key(clothesId, clothesStorageType, userId);
        return amazonS3Service.upload(image, key);
    }

    private String generateImageS3Key(
            Long clothesId,
            ClothesStorageType clothesStorageType,
            Long userId
    ) {
        String name = aesEncryptionUtil.encrypt(clothesId.toString());
        return "users/"
                + userId
                + "/items/"
                + clothesStorageType.name().toLowerCase()
                + "/"
                + name;
    }

    private void checkDuplicateClothesName(String name, Long userId) {
        boolean isDuplicateName = clothesRepository.existsByNameAndUserId(name, userId);
        if (isDuplicateName) {
            throw new IllegalArgumentException("중복된 이름의 옷입니다.");
        }
    }

}
