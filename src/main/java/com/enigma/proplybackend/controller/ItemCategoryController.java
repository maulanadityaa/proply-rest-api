package com.enigma.proplybackend.controller;

import com.enigma.proplybackend.constant.AppPath;
import com.enigma.proplybackend.model.request.ItemCategoryRequest;
import com.enigma.proplybackend.model.response.CommonResponse;
import com.enigma.proplybackend.model.response.ItemCategoryResponse;
import com.enigma.proplybackend.service.ItemCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(AppPath.ITEM_CATEGORY)
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
public class ItemCategoryController {
    private final ItemCategoryService itemCategoryService;

    @PostMapping
    public ResponseEntity<?> addItemCategory(@RequestBody ItemCategoryRequest itemCategoryRequest) {
        ItemCategoryResponse itemCategoryResponse = itemCategoryService.addItemCategory(itemCategoryRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<ItemCategoryResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Item Category created successfully")
                        .data(itemCategoryResponse)
                        .build()
                );
    }

    @PutMapping
    public ResponseEntity<?> updateItemCategory(@RequestBody ItemCategoryRequest itemCategoryRequest) {
        ItemCategoryResponse itemCategoryResponse = itemCategoryService.updateItemCategory(itemCategoryRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<ItemCategoryResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Item Category updated successfully")
                        .data(itemCategoryResponse)
                        .build()
                );
    }

    @GetMapping
    public ResponseEntity<?> getAllItemCategory() {
        List<ItemCategoryResponse> itemCategoryResponseList = itemCategoryService.getAllItemCategories();

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<List<ItemCategoryResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Item Categories retrieved successfully")
                        .data(itemCategoryResponseList)
                        .build()
                );
    }

    @GetMapping(AppPath.ACTIVE_STATUS)
    public ResponseEntity<?> getAllActiveItemCategory() {
        List<ItemCategoryResponse> itemCategoryResponseList = itemCategoryService.getAllItemCategoriesWhereActive();

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<List<ItemCategoryResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Item Categories retrieved successfully")
                        .data(itemCategoryResponseList)
                        .build()
                );
    }

    @GetMapping(AppPath.GET_BY_ID)
    public ResponseEntity<?> getItemCategoryById(@PathVariable String id) {
        ItemCategoryResponse itemCategoryResponse = itemCategoryService.getItemCategoryById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<ItemCategoryResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Item Category retrieved successfully")
                        .data(itemCategoryResponse)
                        .build()
                );
    }

    @DeleteMapping(AppPath.DELETE_BY_ID)
    public ResponseEntity<?> deleteItemCategoryById(@PathVariable String id) {
        itemCategoryService.deleteItemCategory(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<String>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Item Category deleted successfully")
                        .build()
                );
    }
}
