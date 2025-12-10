package ch.devprojects.orderflow.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ch.devprojects.orderflow.dto.OrderResponseDto;

/**
 * Read-only lookup service for individual Orders.
 *
 * Purpose: - Fetch a single order by ID or code - Keep lookup semantics
 * separated from search / pagination logic
 *
 * Design choice: - This service is intentionally separated from
 * OrderQueryService to demonstrate clean SRP (Single Responsibility Principle).
 * - Later, this implementation can switch to JPA without API changes.
 */
@Service
public class OrderLookupService {

	private final List<OrderResponseDto> sampleOrders;

	public OrderLookupService() {
		this.sampleOrders = buildSampleOrders();
	}

	/**
	 * Find a single order by its technical ID.
	 *
	 * @param id order ID
	 * @return Optional containing OrderResponseDto if found
	 */
	public Optional<OrderResponseDto> findById(Long id) {
		if (id == null) {
			return Optional.empty();
		}

		return sampleOrders.stream().filter(order -> id.equals(order.getId())).findFirst();
	}

	/**
	 * Find a single order by its business code (e.g. ORD-2025-0001).
	 *
	 * Case-insensitive.
	 *
	 * @param code order code
	 * @return Optional containing OrderResponseDto if found
	 */
	public Optional<OrderResponseDto> findByCode(String code) {
		if (code == null || code.isBlank()) {
			return Optional.empty();
		}

		String normalized = code.trim().toUpperCase();

		return sampleOrders.stream().filter(order -> normalized.equalsIgnoreCase(order.getCode())).findFirst();
	}

	/*
	 * --------------------------------------------------------------------
	 * Temporary in-memory data for development
	 * ------------------------------------------------------------------
	 */

	private List<OrderResponseDto> buildSampleOrders() {
		return List.of(
				buildOrder(1L, "ORD-2025-0001", "NEW", "Acme GmbH", "Giovanni Suter", new BigDecimal("120.50"),
						LocalDateTime.now().minusDays(2)),
				buildOrder(2L, "ORD-2025-0002", "PROCESSING", "Globex AG", "Anna Keller", new BigDecimal("89.90"),
						LocalDateTime.now().minusDays(1)),
				buildOrder(3L, "ORD-2025-0003", "PAID", "Innotech Solutions", "Mark Weber", new BigDecimal("240.00"),
						LocalDateTime.now().minusHours(12)));
	}

	private OrderResponseDto buildOrder(Long id, String code, String status, String customerName, String assignedTo,
			BigDecimal total, LocalDateTime createdAt) {
		OrderResponseDto dto = new OrderResponseDto();
		dto.setId(id);
		dto.setCode(code);
		dto.setStatus(status);
		dto.setCustomerName(customerName);
		dto.setAssignedTo(assignedTo);
		dto.setTotal(total);
		dto.setCreatedAt(createdAt);
		return dto;
	}
}