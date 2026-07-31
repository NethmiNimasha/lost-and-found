package com.example.lostandfound.controller;

import com.example.lostandfound.entity.Item;
import com.example.lostandfound.entity.ItemStatus;
import com.example.lostandfound.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Item (lost / found) management.
 *
 * Endpoint summary:
 *  GET    /api/items              – List all items (optionally filtered by status)   (authenticated)
 *  GET    /api/items/{id}         – Get item by ID                                   (authenticated)
 *  POST   /api/items              – Report a new lost/found item                     (ADMIN, STAFF)
 *  PUT    /api/items/{id}         – Update an item                                   (ADMIN, STAFF)
 *  DELETE /api/items/{id}         – Delete an item                                   (ADMIN, STAFF)
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    private ItemService itemService;

    // -------------------------------------------------------------------------
    // GET /api/items?status={LOST|FOUND|CLAIMED}
    // -------------------------------------------------------------------------
    @GetMapping
    public List<Item> getAllItems(@RequestParam(required = false) ItemStatus status) {
        logger.info("GET /api/items – status filter={}", status);
        return itemService.getAllItems(status);
    }

    // -------------------------------------------------------------------------
    // GET /api/items/{id}
    // -------------------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        logger.info("GET /api/items/{}", id);
        return itemService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // -------------------------------------------------------------------------
    // POST /api/items
    // -------------------------------------------------------------------------
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Item> createItem(@RequestBody Item item, Authentication authentication) {
        logger.info("POST /api/items – reported by '{}'", authentication.getName());
        Item created = itemService.createItem(item, authentication.getName());
        return ResponseEntity.ok(created);
    }

    // -------------------------------------------------------------------------
    // PUT /api/items/{id}
    // -------------------------------------------------------------------------
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item item) {
        logger.info("PUT /api/items/{}", id);
        Item updated = itemService.updateItem(id, item);
        return ResponseEntity.ok(updated);
    }

    // -------------------------------------------------------------------------
    // DELETE /api/items/{id}
    // -------------------------------------------------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        logger.info("DELETE /api/items/{}", id);
        itemService.deleteItem(id);
        return ResponseEntity.ok("Item deleted successfully.");
    }
}
