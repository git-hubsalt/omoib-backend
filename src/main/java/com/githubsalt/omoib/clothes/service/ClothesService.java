package com.githubsalt.omoib.clothes.service;

import com.githubsalt.omoib.aws.s3.PresignedURLBuilder;
import com.githubsalt.omoib.clothes.domain.Clothes;
import com.githubsalt.omoib.clothes.dto.*;
import com.githubsalt.omoib.clothes.dto.RegisterClothesRequestDTO.RegisterClothesDTO;
import com.githubsalt.omoib.clothes.enums.ClothesType;
import com.githubsalt.omoib.clothes.enums.SeasonType;
import com.githubsalt.omoib.clothes.repository.ClothesRepository;
import com.githubsalt.omoib.global.enums.ClothesStorageType;
import com.githubsalt.omoib.global.service.AmazonS3Service;
import com.githubsalt.omoib.user.domain.User;
import com.githubsalt.omoib.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final PresignedURLBuilder presignedURLBuilder;
    private final AmazonS3Service amazonS3Service;

    @Transactional(readOnly = true)
    public GetClothesResponseDTO getAllClothes(ClothesStorageType clothesStorageType, Long userId) {
        ArrayList<GetClothesResponseDTO.ClothesItemDTO> clothesDtos = new ArrayList<>();
        List<Clothes> clothes = clothesRepository.findAllByClothesStorageTypeAndUserId(clothesStorageType, userId);
        for (Clothes cloth : clothes) {
            ArrayList<String> tagList = new ArrayList<>();
            for (SeasonType seasonType : cloth.getSeasonType()) {
                tagList.add(seasonType.name());
            }

            if (cloth.getClothesType() != null) {
                tagList.add(cloth.getClothesType().getDescription());
            }

            GetClothesResponseDTO.ClothesItemDTO clothesItemDTO = new GetClothesResponseDTO.ClothesItemDTO(
                    cloth.getId(),
                    cloth.getName(),
                    cloth.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
                    tagList,
                    presignedURLBuilder.buildGetPresignedURL(cloth.getImagePath()).toString()
            );
            clothesDtos.add(clothesItemDTO);
        }
        return new GetClothesResponseDTO(clothesDtos);
    }

    @Transactional(readOnly = true)
    public ClothesResponseDTO getClothesList(Long userId) {
        ClothesResponseDTO closet = getClothesList(ClothesStorageType.CLOSET, userId);
        ClothesResponseDTO wish = getClothesList(ClothesStorageType.WISHLIST, userId);

        MergedResult result = mergeClothesDTOs(closet, wish);

        return new ClothesResponseDTO(result.upper(), result.lower(), result.shoes(), result.bag(), result.cap(), result.outer(), result.overall());
    }

    @Transactional(readOnly = true)
    public ClothesResponseDTO getClothesList(ClothesStorageType storageType, Long userId) {
        Map<ClothesType, ArrayList<ClothesResponseDTO.ClothesItemDTO>> typeArrayListMap = Map.of(
                ClothesType.상의, new ArrayList<>(),
                ClothesType.하의, new ArrayList<>(),
                ClothesType.신발, new ArrayList<>(),
                ClothesType.가방, new ArrayList<>(),
                ClothesType.모자, new ArrayList<>(),
                ClothesType.아우터, new ArrayList<>(),
                ClothesType.한벌옷, new ArrayList<>()
        );
        List<Clothes> clothes = clothesRepository.findAllByClothesStorageTypeAndUserId(storageType, userId);
        for (Clothes cloth : clothes) {
            ArrayList<String> tagList = new ArrayList<>();
            for (SeasonType seasonType : cloth.getSeasonType()) {
                tagList.add(seasonType.name());
            }
            ClothesResponseDTO.ClothesItemDTO clothesItemDTO = new ClothesResponseDTO.ClothesItemDTO(
                    cloth.getId(),
                    cloth.getName(),
                    cloth.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
                    tagList,
                    cloth.getImagePath()
            );

            typeArrayListMap.get(cloth.getClothesType()).add(clothesItemDTO);
        }
        return new ClothesResponseDTO(
                typeArrayListMap.get(ClothesType.상의),
                typeArrayListMap.get(ClothesType.하의),
                typeArrayListMap.get(ClothesType.신발),
                typeArrayListMap.get(ClothesType.가방),
                typeArrayListMap.get(ClothesType.모자),
                typeArrayListMap.get(ClothesType.아우터),
                typeArrayListMap.get(ClothesType.한벌옷)
        );
    }

    @Transactional
    public void registerClothes(
        RegisterClothesRequestDTO requestDTO,
        List<MultipartFile> files,
        ClothesStorageType clothesStorageType,
        Long userId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        for (int idx = 0; idx < requestDTO.clothes().size(); idx++) {
            RegisterClothesDTO clothesDTO = requestDTO.clothes().get(idx);
            MultipartFile file = files.get(idx);

            checkDuplicateClothesName(clothesDTO.name(), userId, clothesStorageType);
            Clothes clothes = clothesRepository.save(
                    Clothes.builder()
                            .name(clothesDTO.name())
                            .clothesType(ClothesType.fromDescription(clothesDTO.clothesType()))
                            .seasonType(clothesDTO.seasonTypes())
                            .clothesStorageType(clothesStorageType)
                            .user(user)
                            .build()
            );
            String imagePath = amazonS3Service.uploadClothes(
                    file, userId, clothesStorageType, clothes.getName()
            );
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
            String imagePath = amazonS3Service.uploadClothes(
                    image, userId, clothes.getClothesStorageType(), clothes.getName()
            );
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
                clothes.getClothesType().getDescription(),
                clothes.getSeasonType(),
                presignedURLBuilder.buildGetPresignedURL(clothes.getImagePath()).toString()
        );
    }

    public ClothesWithPresignedDTO withPresignedDTO(Clothes clothes) {
        return new ClothesWithPresignedDTO(
                clothes.getId(),
                clothes.getName(),
                clothes.getClothesType(),
                clothes.getSeasonType(),
                presignedURLBuilder.buildGetPresignedURL(clothes.getImagePath()).toString(),
                clothes.getClothesStorageType(),
                clothes.getCreateAt(),
                clothes.getUpdateAt());
    }

    private static MergedResult mergeClothesDTOs(ClothesResponseDTO closet, ClothesResponseDTO wish) {
        List<ClothesResponseDTO.ClothesItemDTO> upper = new ArrayList<>(closet.upper());
        upper.addAll(wish.upper());

        List<ClothesResponseDTO.ClothesItemDTO> lower = new ArrayList<>(closet.lower());
        lower.addAll(wish.lower());

        List<ClothesResponseDTO.ClothesItemDTO> shoes = new ArrayList<>(closet.shoes());
        shoes.addAll(wish.shoes());

        List<ClothesResponseDTO.ClothesItemDTO> bag = new ArrayList<>(closet.bag());
        bag.addAll(wish.bag());

        List<ClothesResponseDTO.ClothesItemDTO> cap = new ArrayList<>(closet.cap());
        cap.addAll(wish.cap());

        List<ClothesResponseDTO.ClothesItemDTO> outer = new ArrayList<>(closet.outer());
        outer.addAll(wish.outer());

        List<ClothesResponseDTO.ClothesItemDTO> overall = new ArrayList<>(closet.overall());
        overall.addAll(wish.overall());

        return new MergedResult(upper, lower, shoes, bag, cap, outer, overall);
    }

    private record MergedResult(List<ClothesResponseDTO.ClothesItemDTO> upper,
                                List<ClothesResponseDTO.ClothesItemDTO> lower,
                                List<ClothesResponseDTO.ClothesItemDTO> shoes,
                                List<ClothesResponseDTO.ClothesItemDTO> bag,
                                List<ClothesResponseDTO.ClothesItemDTO> cap,
                                List<ClothesResponseDTO.ClothesItemDTO> outer,
                                List<ClothesResponseDTO.ClothesItemDTO> overall) {
    }

    private void checkDuplicateClothesName(String name, Long userId, ClothesStorageType clothesStorageType) {
        boolean isDuplicateName = clothesRepository.existsByNameAndUserIdAndClothesStorageType(
                name, userId, clothesStorageType
        );
        if (isDuplicateName) {
            throw new IllegalArgumentException("중복된 이름의 옷입니다.");
        }
    }

}
