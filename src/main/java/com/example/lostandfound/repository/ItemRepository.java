package com.example.lostandfound.repository;

import com.example.lostandfound.entity.Item;
import com.example.lostandfound.entity.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Returns all items with the given status (LOST, FOUND, or CLAIMED).
     * Used by GET /api/items?status=... to support optional status filtering.
     */
    List<Item> findByStatus(ItemStatus status);
}
