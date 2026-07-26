package com.example.lostandfound.service;

import com.example.lostandfound.entity.Item;
import com.example.lostandfound.entity.ItemStatus;
import com.example.lostandfound.entity.User;
import com.example.lostandfound.repository.ItemRepository;
import com.example.lostandfound.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    public Item createItem(Item item, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        item.setReportedBy(user);
        if (item.getStatus() == null) {
            item.setStatus(ItemStatus.LOST);
        }
        return itemRepository.save(item);
    }

    public Item updateItem(Long id, Item itemDetails) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setTitle(itemDetails.getTitle());
        item.setDescription(itemDetails.getDescription());
        if (itemDetails.getStatus() != null) {
            item.setStatus(itemDetails.getStatus());
        }
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        itemRepository.delete(item);
    }
}
