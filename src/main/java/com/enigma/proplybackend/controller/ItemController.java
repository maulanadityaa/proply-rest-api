package com.enigma.proplybackend.controller;

import com.enigma.proplybackend.constant.AppPath;
import com.enigma.proplybackend.model.request.ItemRequest;
import com.enigma.proplybackend.model.response.CommonResponse;
import com.enigma.proplybackend.model.response.ItemResponse;
import com.enigma.proplybackend.service.ItemService;
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
@RequestMapping(AppPath.ITEM)
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<?> addItem(@RequestBody ItemRequest itemRequest) {
        ItemResponse itemResponse = itemService.addItem(itemRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<ItemResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Item added successfully")
                        .data(itemResponse)
                        .build()
                );
    }

    @PutMapping
    public ResponseEntity<?> updateItem(@RequestBody ItemRequest itemRequest) {
        ItemResponse itemResponse = itemService.updateItem(itemRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<ItemResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Item updated successfully")
                        .data(itemResponse)
                        .build()
                );
    }

    @GetMapping
    public ResponseEntity<?> getAllItems() {
        List<ItemResponse> itemResponseList = itemService.getAllItems();

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<List<ItemResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Items retrieved successfully")
                        .data(itemResponseList)
                        .build()
                );
    }

    @GetMapping(AppPath.ACTIVE_STATUS)
    public ResponseEntity<?> getAllActiveItems() {
        List<ItemResponse> itemResponseList = itemService.getAllItemsWhereActive();

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<List<ItemResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Active items retrieved successfully")
                        .data(itemResponseList)
                        .build()
                );
    }

    @GetMapping(AppPath.GET_BY_ID)
    public ResponseEntity<?> getByID(@PathVariable String id) {
        ItemResponse itemResponse = itemService.getItemById(id);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<ItemResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Item retrieved successfully")
                        .data(itemResponse)
                        .build()
                );
    }

    @DeleteMapping(AppPath.DELETE_BY_ID)
    public ResponseEntity<?> deleteItemById(@PathVariable String id) {
        itemService.deleteItem(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<String>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Item deleted successfully")
                        .build()
                );
    }
}
