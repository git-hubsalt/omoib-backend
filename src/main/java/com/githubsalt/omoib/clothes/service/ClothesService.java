package com.githubsalt.omoib.clothes.service;

import com.githubsalt.omoib.aws.s3.PresignedURLBuilder;
import com.githubsalt.omoib.clothes.domain.Clothes;
import com.githubsalt.omoib.clothes.dto.BriefClothesDTO;
import com.githubsalt.omoib.clothes.dto.ClothesResponseDTO;
import com.githubsalt.omoib.clothes.dto.GetClothesResponseDTO;
import com.githubsalt.omoib.clothes.dto.RegisterClothesRequestDTO;
import com.githubsalt.omoib.clothes.dto.RegisterClothesRequestDTO.RegisterClothesDTO;
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
    public GetClothesResponseDTO getAllClothes(Long userId) {
        ArrayList<GetClothesResponseDTO.ClothesItemDTO> clothesDtos = new ArrayList<>();
        List<Clothes> clothes = clothesRepository.findAllByUserId(userId);
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
                ClothesType.upper, new ArrayList<>(),
                ClothesType.lower, new ArrayList<>(),
                ClothesType.shoes, new ArrayList<>(),
                ClothesType.bag, new ArrayList<>(),
                ClothesType.cap, new ArrayList<>(),
                ClothesType.outer, new ArrayList<>(),
                ClothesType.overall, new ArrayList<>()
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
        List<MultipartFile> files,
        ClothesStorageType clothesStorageType,
        Long userId
    ) {
        for (int idx = 0; idx < requestDTO.clothes().size(); idx++) {
            RegisterClothesDTO clothesDTO = requestDTO.clothes().get(idx);
            MultipartFile file = files.get(idx);

            checkDuplicateClothesName(clothesDTO.name(), userId);
            Clothes clothes = clothesRepository.save(
                    Clothes.builder()
                            .name(clothesDTO.name())
                            .clothesType(ClothesType.fromDescription(clothesDTO.clothesType()))
                            .seasonType(clothesDTO.seasonType())
                            .clothesStorageType(clothesStorageType)
                            .build()
            );
            String imagePath = uploadS3Image(file, clothes.getId(), clothesStorageType, userId);
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
