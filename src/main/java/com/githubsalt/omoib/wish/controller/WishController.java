package com.githubsalt.omoib.wish.controller;

import com.githubsalt.omoib.clothes.dto.RegisterClothesRequestDTO;
import com.githubsalt.omoib.clothes.dto.UpdateClothesRequestDTO;
import com.githubsalt.omoib.clothes.service.ClothesService;
import com.githubsalt.omoib.global.enums.ClothesStorageType;
import com.githubsalt.omoib.wish.dto.GetWishResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/wish")
@RequiredArgsConstructor
public class WishController {

    private final ClothesService clothesService;

    @GetMapping
    public ResponseEntity<GetWishResponseDTO> getCloset() {
        clothesService.getClothesList(ClothesStorageType.Wish);
        //TODO
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> registerClothes(
        @RequestPart RegisterClothesRequestDTO requestDTO,
        @RequestPart MultipartFile image
    ) {
        clothesService.registerClothes(requestDTO, image);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateClothes(
        @PathVariable("id") Long clothesId,
        @RequestPart UpdateClothesRequestDTO requestDTO,
        @RequestPart MultipartFile image
    ) {
        clothesService.updateClothes(clothesId, requestDTO, image);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeClothes(
        @PathVariable("id") Long clothesId
    ) {
        clothesService.removeClothes(clothesId);
        return ResponseEntity.ok().build();
    }


}