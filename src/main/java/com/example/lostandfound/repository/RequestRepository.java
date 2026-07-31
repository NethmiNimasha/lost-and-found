package com.example.lostandfound.repository;

import com.example.lostandfound.entity.Request;
import com.example.lostandfound.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByUserId(Long userId);

    List<Request> findByItemId(Long itemId);

    /**
     * Returns true if a request with the given userId, itemId and status already exists.
     * Used to prevent duplicate PENDING requests from the same user for the same item.
     */
    boolean existsByUserIdAndItemIdAndStatus(Long userId, Long itemId, RequestStatus status);
}
