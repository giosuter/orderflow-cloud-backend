package ch.devprojects.orderflow.service;

import java.util.List;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;

/**
 * Service contract for managing Orders (CRUD + simple search).
 *
 * Notes: - Controller depends only on this interface (easy mocking in tests). -
 * DTO uses "description" as canonical free-text field.
 */
public interface OrderService {

	/**
	 * Create an order.
	 */
	OrderDto create(OrderDto dto);

	/**
	 * Find a single order by database id.
	 */
	OrderDto findById(Long id);

	/**
	 * Get all orders (no paging). Useful for very small datasets / quick demos.
	 */
	List<OrderDto> findAll();

	/**
	 * Update an existing order.
	 */
	OrderDto update(Long id, OrderDto dto);

	/**
	 * Delete an order by id.
	 */
	void delete(Long id);

	/**
	 * Search orders by optional filters.
	 *
	 * @param code   optional substring to search in the order code
	 *               (case-insensitive)
	 * @param status optional exact status to filter on
	 * @return list of matching orders (possibly empty)
	 */
	List<OrderDto> search(String code, OrderStatus status);

	/**
	 * Find one order by its business code.
	 */
	OrderDto findByCode(String code);
}