package com.githubsalt.omoib.clothes.service;

import com.githubsalt.omoib.clothes.domain.Clothes;
import com.githubsalt.omoib.clothes.dto.BriefClothesDTO;
import com.githubsalt.omoib.clothes.dto.RegisterClothesRequestDTO;
import com.githubsalt.omoib.clothes.dto.UpdateClothesRequestDTO;
import com.githubsalt.omoib.clothes.dto.GetClothesListDTO;
import com.githubsalt.omoib.clothes.repository.ClothesRepository;
import com.githubsalt.omoib.global.enums.ClothesStorageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ClothesService {

    private final ClothesRepository clothesRepository;

    public GetClothesListDTO getClothesList(ClothesStorageType clothesStorageType) {
        //TODO
        return new GetClothesListDTO();
    }

    public void registerClothes(RegisterClothesRequestDTO requestDTO, MultipartFile image) {
        //TODO
    }

    public void updateClothes(Long clothesId, UpdateClothesRequestDTO requestDTO, MultipartFile image) {
        //TODO
    }

    public void removeClothes(Long clothesId) {
        //TODO
    }

    public Clothes getClothes(Long clothesId) {
        return clothesRepository.findById(clothesId).orElse(null);
    }

    public BriefClothesDTO getBriefClothes(Long clothesId) {
        //TODO
        return new BriefClothesDTO(1L, "name", null, null, "url");
    }

}
