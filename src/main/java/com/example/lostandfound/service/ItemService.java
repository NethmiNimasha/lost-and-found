package com.example.lostandfound.service;

import com.example.lostandfound.entity.Item;
import com.example.lostandfound.entity.ItemStatus;
import com.example.lostandfound.entity.User;
import com.example.lostandfound.repository.ItemRepository;
import com.example.lostandfound.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Item management.
 * Handles LOST / FOUND / CLAIMED item lifecycle.
 */
@Service
public class ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Returns all items, optionally filtered by status.
     *
     * @param status optional filter (LOST, FOUND, CLAIMED). Pass null to get all items.
     * @return list of items
     */
    public List<Item> getAllItems(ItemStatus status) {
        if (status != null) {
            logger.debug("Fetching all items with status={}", status);
            List<Item> filtered = itemRepository.findByStatus(status);
            logger.info("Retrieved {} item(s) with status={}", filtered.size(), status);
            return filtered;
        }
        logger.debug("Fetching all items (no status filter)");
        List<Item> all = itemRepository.findAll();
        logger.info("Retrieved {} item(s)", all.size());
        return all;
    }

    /**
     * Finds an item by its ID.
     *
     * @param id the item's primary key
     * @return Optional containing the item, or empty if not found
     */
    public Optional<Item> getItemById(Long id) {
        logger.debug("Fetching item with id={}", id);
        return itemRepository.findById(id);
    }

    /**
     * Creates a new item and associates it with the authenticated user.
     * If no status is provided, defaults to LOST.
     *
     * @param item     the item data from the request body
     * @param username the username of the authenticated reporter
     * @return the saved Item entity
     */
    public Item createItem(Item item, String username) {
        logger.debug("Creating item '{}' for user '{}'", item.getTitle(), username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Item creation failed – user not found: {}", username);
                    return new RuntimeException("User not found: " + username);
                });

        item.setReportedBy(user);
        if (item.getStatus() == null) {
            item.setStatus(ItemStatus.LOST);
        }

        Item saved = itemRepository.save(item);
        logger.info("Item created: id={}, title='{}', status={}, reportedBy='{}'",
                saved.getId(), saved.getTitle(), saved.getStatus(), username);
        return saved;
    }

    /**
     * Updates an existing item's fields.
     * All updatable fields (title, description, location, contactInfo, status) are applied
     * if they are present in the request body.
     *
     * @param id          the ID of the item to update
     * @param itemDetails the request body with new values
     * @return the updated and persisted Item
     */
    public Item updateItem(Long id, Item itemDetails) {
        logger.debug("Attempting to update item with id={}", id);

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Update failed – item not found with id={}", id);
                    return new RuntimeException("Item not found with id: " + id);
                });

        if (itemDetails.getTitle() != null && !itemDetails.getTitle().isBlank()) {
            item.setTitle(itemDetails.getTitle());
        }
        if (itemDetails.getDescription() != null && !itemDetails.getDescription().isBlank()) {
            item.setDescription(itemDetails.getDescription());
        }
        if (itemDetails.getLocation() != null) {
            item.setLocation(itemDetails.getLocation());
        }
        if (itemDetails.getContactInfo() != null) {
            item.setContactInfo(itemDetails.getContactInfo());
        }
        if (itemDetails.getStatus() != null) {
            item.setStatus(itemDetails.getStatus());
        }

        Item saved = itemRepository.save(item);
        logger.info("Item updated: id={}, title='{}', status={}", saved.getId(), saved.getTitle(), saved.getStatus());
        return saved;
    }

    /**
     * Permanently deletes an item by ID.
     *
     * @param id the ID of the item to delete
     */
    public void deleteItem(Long id) {
        logger.debug("Attempting to delete item with id={}", id);

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Delete failed – item not found with id={}", id);
                    return new RuntimeException("Item not found with id: " + id);
                });

        itemRepository.delete(item);
        logger.info("Item deleted: id={}, title='{}'", id, item.getTitle());
    }
}
