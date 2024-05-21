package com.enigma.proplybackend.service;

import com.enigma.proplybackend.model.request.ItemRequest;
import com.enigma.proplybackend.model.response.ItemResponse;

import java.util.List;

public interface ItemService {
    ItemResponse addItem(ItemRequest itemRequest);

    ItemResponse updateItem(ItemRequest itemRequest);

    void deleteItem(String itemId);

    ItemResponse getItemById(String itemId);

    List<ItemResponse> getAllItems();

    List<ItemResponse> getAllItemsWhereActive();
}
