package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.model.entity.Item;
import com.enigma.proplybackend.model.entity.ItemCategory;
import com.enigma.proplybackend.model.exception.ApplicationException;
import com.enigma.proplybackend.model.request.ItemRequest;
import com.enigma.proplybackend.model.response.ItemCategoryResponse;
import com.enigma.proplybackend.model.response.ItemResponse;
import com.enigma.proplybackend.repository.ItemRepository;
import com.enigma.proplybackend.service.ItemCategoryService;
import com.enigma.proplybackend.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemCategoryService itemCategoryService;

    @Override
    public ItemResponse addItem(ItemRequest itemRequest) {
        try {
            ItemCategoryResponse itemCategoryResponse = itemCategoryService.getItemCategoryById(itemRequest.getItemCategoryId());
            ItemCategory itemCategory = ItemCategory.builder()
                    .id(itemCategoryResponse.getItemCategoryId())
                    .name(itemCategoryResponse.getName())
                    .isActive(itemCategoryResponse.getIsActive())
                    .build();

            Item item = Item.builder()
                    .name(itemRequest.getName())
                    .isActive(true)
                    .itemCategory(itemCategory)
                    .createdAt(Instant.now().getEpochSecond())
                    .updatedAt(Instant.now().getEpochSecond())
                    .build();
            itemRepository.save(item);

            return toItemResponse(item, itemCategoryResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException("Cannot create item", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ItemResponse updateItem(ItemRequest itemRequest) {
        Item item = itemRepository.findById(itemRequest.getId()).orElseThrow(() -> new ApplicationException("Could not find item", "Item with id=" + itemRequest.getId() + " was not found", HttpStatus.NOT_FOUND));

        try {
            ItemCategoryResponse itemCategoryResponse = itemCategoryService.getItemCategoryById(itemRequest.getItemCategoryId());
            ItemCategory itemCategory = ItemCategory.builder()
                    .id(itemCategoryResponse.getItemCategoryId())
                    .name(itemCategoryResponse.getName())
                    .isActive(itemCategoryResponse.getIsActive())
                    .build();

            item.setName(itemRequest.getName());
            item.setItemCategory(itemCategory);
            item.setUpdatedAt(Instant.now().getEpochSecond());
            itemRepository.save(item);

            return toItemResponse(item, itemCategoryResponse);
        } catch (Exception e) {
            throw new ApplicationException("Cannot update item", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void deleteItem(String itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ApplicationException("Item not found", "Item with id=" + itemId + " not found", HttpStatus.NOT_FOUND));

        item.setIsActive(false);
        item.setUpdatedAt(Instant.now().getEpochSecond());
        itemRepository.save(item);
    }

    @Override
    public ItemResponse getItemById(String itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ApplicationException("Item not found", "Item with id=" + itemId + " not found", HttpStatus.NOT_FOUND));
        ItemCategoryResponse itemCategoryResponse = itemCategoryService.getItemCategoryById(item.getItemCategory().getId());

        return toItemResponse(item, itemCategoryResponse);
    }

    @Override
    public List<ItemResponse> getAllItems() {
        List<Item> items = itemRepository.findAll();
        List<ItemResponse> itemResponseList = new ArrayList<>();

        if (items.isEmpty()) throw new ApplicationException("No items found", null, HttpStatus.NOT_FOUND);

        for (Item item : items) {
            ItemCategoryResponse itemCategoryResponse = itemCategoryService.getItemCategoryById(item.getItemCategory().getId());
            itemResponseList.add(toItemResponse(item, itemCategoryResponse));
        }

        return itemResponseList;
    }

    @Override
    public List<ItemResponse> getAllItemsWhereActive() {
        List<Item> items = itemRepository.findAll();
        List<ItemResponse> itemResponseList = new ArrayList<>();

        if (items.isEmpty()) throw new ApplicationException("No items found", null, HttpStatus.NOT_FOUND);

        for (Item item : items) {
            if (item.getIsActive()) {
                ItemCategoryResponse itemCategoryResponse = itemCategoryService.getItemCategoryById(item.getItemCategory().getId());
                itemResponseList.add(toItemResponse(item, itemCategoryResponse));
            }
        }

        return itemResponseList;
    }

    private static ItemResponse toItemResponse(Item item, ItemCategoryResponse itemCategoryResponse) {
        return ItemResponse.builder()
                .itemId(item.getId())
                .name(item.getName())
                .itemCategoryResponse(itemCategoryResponse)
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .isActive(item.getIsActive())
                .build();
    }
}
