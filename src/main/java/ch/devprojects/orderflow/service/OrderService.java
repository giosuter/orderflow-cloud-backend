package ch.devprojects.orderflow.service;

import java.util.List;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;

/**
 * Service contract for managing Orders.
 *
 * This interface:
 * - decouples the controller from the implementation (OrderServiceImpl).
 * - makes unit testing easier (we can mock OrderService if needed).
 * - helps if you ever want multiple implementations (unlikely here, but clean design).
 */
public interface OrderService {

    /**
     * Create a new order based on the given DTO.
     */
    OrderDto create(OrderDto dto);

    /**
     * Find a single order by its id.
     *
     * @throws jakarta.persistence.EntityNotFoundException if the id does not exist
     */
    OrderDto findById(Long id);

    /**
     * Returns all orders as a simple list (no pagination).
     */
    List<OrderDto> findAll();

    /**
     * Update an existing order with the given id.
     *
     * @throws jakarta.persistence.EntityNotFoundException if the id does not exist
     */
    OrderDto update(Long id, OrderDto dto);

    /**
     * Delete an order by id.
     *
     * @throws jakarta.persistence.EntityNotFoundException if the id does not exist
     */
    void delete(Long id);

    /**
     * Search orders by optional filters.
     *
     * Why this method?
     * - Provides a flexible search API without exposing JPA specifics.
     * - Uses Specifications under the hood (OrderSpecifications).
     *
     * @param code   optional substring to search in the order code (case-insensitive)
     * @param status optional exact status to filter on
     * @return list of matching orders (possibly empty)
     */
    List<OrderDto> search(String code, OrderStatus status);
}