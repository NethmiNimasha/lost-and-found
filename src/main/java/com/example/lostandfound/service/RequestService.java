package com.example.lostandfound.service;

import com.example.lostandfound.entity.Item;
import com.example.lostandfound.entity.ItemStatus;
import com.example.lostandfound.entity.Request;
import com.example.lostandfound.entity.RequestStatus;
import com.example.lostandfound.entity.User;
import com.example.lostandfound.repository.ItemRepository;
import com.example.lostandfound.repository.RequestRepository;
import com.example.lostandfound.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for claim Request management.
 *
 * Business rules:
 * <ul>
 *   <li>A request defaults to PENDING on creation.</li>
 *   <li>A user may not submit a duplicate PENDING request for the same item.</li>
 *   <li>Approving a request (status → APPROVED) automatically marks the
 *       linked item as CLAIMED.</li>
 * </ul>
 */
@Service
public class RequestService {

    private static final Logger logger = LoggerFactory.getLogger(RequestService.class);

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Returns all claim requests (ADMIN / STAFF view).
     */
    public List<Request> getAllRequests() {
        logger.debug("Fetching all requests");
        List<Request> requests = requestRepository.findAll();
        logger.info("Retrieved {} request(s)", requests.size());
        return requests;
    }

    /**
     * Returns all requests submitted by a specific user.
     *
     * @param userId the user's primary key
     * @return list of requests belonging to that user
     */
    public List<Request> getRequestsByUser(Long userId) {
        logger.debug("Fetching requests for userId={}", userId);
        List<Request> requests = requestRepository.findByUserId(userId);
        logger.info("Retrieved {} request(s) for userId={}", requests.size(), userId);
        return requests;
    }

    /**
     * Finds a single request by its ID.
     */
    public Optional<Request> getRequestById(Long id) {
        logger.debug("Fetching request with id={}", id);
        return requestRepository.findById(id);
    }

    /**
     * Creates a new PENDING claim request for the given item.
     *
     * <p>Prevents duplicate PENDING requests from the same user for the same item.</p>
     *
     * @param itemId   the ID of the item being claimed
     * @param username the username of the authenticated claimant
     * @return the persisted Request entity
     */
    @Transactional
    public Request createRequest(Long itemId, String username) {
        logger.debug("Creating claim request for itemId={} by user='{}'", itemId, username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Request creation failed – user not found: {}", username);
                    return new RuntimeException("User not found: " + username);
                });

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    logger.warn("Request creation failed – item not found with id={}", itemId);
                    return new RuntimeException("Item not found with id: " + itemId);
                });

        // Business rule: no duplicate PENDING requests for the same user + item
        boolean duplicateExists = requestRepository.existsByUserIdAndItemIdAndStatus(
                user.getId(), itemId, RequestStatus.PENDING);

        if (duplicateExists) {
            logger.warn("Duplicate request blocked – user='{}' already has a pending request for itemId={}", username, itemId);
            throw new RuntimeException("You already have a pending claim request for this item.");
        }

        Request request = new Request(user, item, RequestStatus.PENDING);
        Request saved = requestRepository.save(request);
        logger.info("Claim request created: id={}, itemId={}, userId={}, status={}",
                saved.getId(), itemId, user.getId(), saved.getStatus());
        return saved;
    }

    /**
     * Updates the status of a request (PENDING → APPROVED / REJECTED).
     *
     * <p><strong>Business rule:</strong> When a request is APPROVED, the linked item's
     * status is automatically updated to CLAIMED.</p>
     *
     * @param id     the request ID
     * @param status the new RequestStatus
     * @return the updated and persisted Request
     */
    @Transactional
    public Request updateRequestStatus(Long id, RequestStatus status) {
        logger.debug("Updating status of requestId={} to {}", id, status);

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Status update failed – request not found with id={}", id);
                    return new RuntimeException("Request not found with id: " + id);
                });

        request.setStatus(status);

        // Business rule: APPROVED request → mark the linked item as CLAIMED
        // We load the item via itemRepository (not request.getItem()) to avoid
        // lazy-load proxy issues when FetchType.LAZY is used on the relation.
        if (status == RequestStatus.APPROVED) {
            Long itemId = request.getItem().getId();
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));
            item.setStatus(ItemStatus.CLAIMED);
            itemRepository.save(item);
            logger.info("Item id={} marked as CLAIMED following approval of requestId={}", itemId, id);
        }

        Request saved = requestRepository.save(request);
        logger.info("Request id={} status updated to {}", id, status);
        return saved;
    }

    /**
     * Permanently deletes a request by ID.
     *
     * @param id the request ID
     */
    public void deleteRequest(Long id) {
        logger.debug("Attempting to delete requestId={}", id);

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Delete failed – request not found with id={}", id);
                    return new RuntimeException("Request not found with id: " + id);
                });

        requestRepository.delete(request);
        logger.info("Request deleted: id={}", id);
    }
}
