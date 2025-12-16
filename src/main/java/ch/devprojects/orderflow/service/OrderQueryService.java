package ch.devprojects.orderflow.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.dto.OrdersPageResponse;

/**
 * Query-oriented service for searching and paging Orders.
 *
 * Why separate from OrderService? - OrderService focuses on CRUD -
 * OrderQueryService focuses on complex filter/paging/sorting
 *
 * Important: - The DTO carries "description" (canonical free-text field).
 */
public interface OrderQueryService {

	Page<OrderDto> search(String customer, OrderStatus status, String codeFrom, String codeTo, BigDecimal totalMin,
			BigDecimal totalMax, Pageable pageable);

	OrdersPageResponse findOrders(String customer, OrderStatus status, int page, int size, String sortBy,
			String sortDir, BigDecimal totalMin, BigDecimal totalMax);

	OrdersPageResponse findOrders(String customer, OrderStatus status, int page, int size);
}