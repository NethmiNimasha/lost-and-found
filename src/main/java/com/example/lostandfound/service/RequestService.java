package com.example.lostandfound.service;

import com.example.lostandfound.entity.Item;
import com.example.lostandfound.entity.Request;
import com.example.lostandfound.entity.RequestStatus;
import com.example.lostandfound.entity.User;
import com.example.lostandfound.repository.ItemRepository;
import com.example.lostandfound.repository.RequestRepository;
import com.example.lostandfound.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public Optional<Request> getRequestById(Long id) {
        return requestRepository.findById(id);
    }

    public Request createRequest(Long itemId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        Request request = new Request(user, item, RequestStatus.PENDING);
        return requestRepository.save(request);
    }

    public Request updateRequestStatus(Long id, RequestStatus status) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(status);
        return requestRepository.save(request);
    }

    public void deleteRequest(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        requestRepository.delete(request);
    }
}
