package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.model.entity.ItemCategory;
import com.enigma.proplybackend.model.exception.ApplicationException;
import com.enigma.proplybackend.model.request.ItemCategoryRequest;
import com.enigma.proplybackend.model.response.ItemCategoryResponse;
import com.enigma.proplybackend.repository.ItemCategoryRepository;
import com.enigma.proplybackend.service.ItemCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemCategoryServiceImpl implements ItemCategoryService {
    private final ItemCategoryRepository itemCategoryRepository;

    @Override
    public ItemCategoryResponse addItemCategory(ItemCategoryRequest itemCategoryRequest) {
        try {
            ItemCategory itemCategory = toItemCategory(itemCategoryRequest);
            itemCategoryRepository.save(itemCategory);

            return toItemCategoryResponse(itemCategory);
        } catch (Exception e) {
            throw new ApplicationException("Cannot create item category", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ItemCategoryResponse updateItemCategory(ItemCategoryRequest itemCategoryRequest) {
        ItemCategory itemCategory = itemCategoryRepository.findById(itemCategoryRequest.getItemCategoryId()).orElseThrow(() -> new ApplicationException("Cannot find item category", "Item category with id=" + itemCategoryRequest.getItemCategoryId() + " not found", HttpStatus.NOT_FOUND));

        itemCategory.setName(itemCategoryRequest.getName());
        itemCategoryRepository.save(itemCategory);

        return toItemCategoryResponse(itemCategory);
    }

    @Override
    public void deleteItemCategory(String itemCategoryId) {
        ItemCategory itemCategory = itemCategoryRepository.findById(itemCategoryId).orElseThrow(() -> new ApplicationException("Cannot find item category", "Item category with id=" + itemCategoryId + " not found", HttpStatus.NOT_FOUND));

        itemCategory.setIsActive(false);
        itemCategoryRepository.save(itemCategory);
    }

    @Override
    public ItemCategoryResponse getItemCategoryById(String itemCategoryId) {
        ItemCategory itemCategory = itemCategoryRepository.findById(itemCategoryId).orElseThrow(() -> new ApplicationException("Cannot find item category", "Item category with id=" + itemCategoryId + " not found", HttpStatus.NOT_FOUND));

        return toItemCategoryResponse(itemCategory);
    }

    @Override
    public List<ItemCategoryResponse> getAllItemCategories() {
        List<ItemCategory> itemCategories = itemCategoryRepository.findAll();
        List<ItemCategoryResponse> itemCategoryResponseList = new ArrayList<>();

        if (itemCategories.isEmpty())
            throw new ApplicationException("No item category found", null, HttpStatus.NOT_FOUND);

        for (ItemCategory itemCategory : itemCategories) {
            itemCategoryResponseList.add(toItemCategoryResponse(itemCategory));
        }

        return itemCategoryResponseList;
    }

    @Override
    public List<ItemCategoryResponse> getAllItemCategoriesWhereActive() {
        List<ItemCategory> itemCategories = itemCategoryRepository.findAll();
        List<ItemCategoryResponse> itemCategoryResponseList = new ArrayList<>();

        if (itemCategories.isEmpty())
            throw new ApplicationException("No item category found", null, HttpStatus.NOT_FOUND);

        for (ItemCategory itemCategory : itemCategories) {
            if (itemCategory.getIsActive()) {
                itemCategoryResponseList.add(toItemCategoryResponse(itemCategory));
            }
        }

        return itemCategoryResponseList;
    }

    private static ItemCategoryResponse toItemCategoryResponse(ItemCategory itemCategory) {
        return ItemCategoryResponse.builder()
                .itemCategoryId(itemCategory.getId())
                .name(itemCategory.getName())
                .isActive(itemCategory.getIsActive())
                .build();
    }

    private static ItemCategory toItemCategory(ItemCategoryRequest itemCategoryRequest) {
        return ItemCategory.builder()
                .name(itemCategoryRequest.getName())
                .isActive(true)
                .build();
    }
}
