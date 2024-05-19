package com.enigma.proplybackend.repository;

import com.enigma.proplybackend.model.entity.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCategoryRepository extends JpaRepository<ItemCategory, String> {
}
