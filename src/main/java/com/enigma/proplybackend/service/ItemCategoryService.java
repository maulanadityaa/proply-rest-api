package com.enigma.proplybackend.service;

import com.enigma.proplybackend.model.request.ItemCategoryRequest;
import com.enigma.proplybackend.model.response.ItemCategoryResponse;

import java.util.List;

public interface ItemCategoryService {
    ItemCategoryResponse addItemCategory(ItemCategoryRequest itemCategoryRequest);

    ItemCategoryResponse updateItemCategory(ItemCategoryRequest itemCategoryRequest);

    void deleteItemCategory(String itemCategoryId);

    ItemCategoryResponse getItemCategoryById(String itemCategoryId);

    List<ItemCategoryResponse> getAllItemCategories();

    List<ItemCategoryResponse> getAllItemCategoriesWhereActive();
}
