package com.githubsalt.omoib.closet.controller;

import com.githubsalt.omoib.closet.dto.GetClosetResponseDTO;
import com.githubsalt.omoib.clothes.dto.RegisterClothesRequestDTO;
import com.githubsalt.omoib.clothes.dto.UpdateClothesRequestDTO;
import com.githubsalt.omoib.clothes.service.ClothesService;
import com.githubsalt.omoib.global.enums.ClothesStorageType;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/closet")
@RequiredArgsConstructor
public class ClosetController {

    private final ClothesService clothesService;

    @Operation(summary = "옷장 조회",
        description = "옷장에 등록한 옷 목록을 옷 카테고리별로 분류하여 반환합니다.")
    @GetMapping
    public ResponseEntity<GetClosetResponseDTO> getCloset() {
        clothesService.getClothesList(ClothesStorageType.Closet);
        //TODO
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "옷장에 옷 등록 (옷 정보, 옷 이미지)",
        description = "옷장에 새로운 옷을 등록합니다.")
    @PostMapping
    public ResponseEntity<Void> registerClothes(
        @RequestPart RegisterClothesRequestDTO requestDTO,
        @RequestPart MultipartFile image
    ) {
        clothesService.registerClothes(requestDTO, image);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "옷장에 옷 수정 (ID)",
        description = "옷장에 등록했던 옷을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateClothes(
        @PathVariable("id") Long clothesId,
        @RequestPart UpdateClothesRequestDTO requestDTO,
        @RequestPart MultipartFile image
    ) {
        clothesService.updateClothes(clothesId, requestDTO, image);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "옷장에 옷 삭제 (ID)",
        description = "옷장에 등록했던 옷을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeClothes(
        @PathVariable("id") Long clothesId
    ) {
        clothesService.removeClothes(clothesId);
        return ResponseEntity.ok().build();
    }


}