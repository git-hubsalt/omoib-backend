package com.githubsalt.omoib.clothes.dto;

import java.util.ArrayList;
import java.util.List;

public record GetClothesResponseDTO(
    List<ClothesItemDTO> upper,
    List<ClothesItemDTO> lower,
    List<ClothesItemDTO> shoes,
    List<ClothesItemDTO> bag,
    List<ClothesItemDTO> cap,
    List<ClothesItemDTO> outer,
    List<ClothesItemDTO> overall
) {

    public List<ClothesItemDTO> getAllClothesItems() {
        List<ClothesItemDTO> allItems = new ArrayList<>();
        allItems.addAll(upper);
        allItems.addAll(lower);
        allItems.addAll(shoes);
        allItems.addAll(bag);
        allItems.addAll(cap);
        allItems.addAll(outer);
        allItems.addAll(overall);
        return allItems;
    }

    public record ClothesItemDTO(
        Long id,
        String name,
        String createDate,
        List<String> tagList,
        String imageUrl
    ){
    }
}
