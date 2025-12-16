package ch.devprojects.orderflow.service;

import java.util.Optional;

import ch.devprojects.orderflow.dto.OrderResponseDto;

/**
 * Read-only lookup service for Orders.
 *
 * Purpose: - Lightweight queries - No write operations - Returns API-facing
 * DTOs (OrderResponseDto)
 */
public interface OrderLookupService {

	/**
	 * Lookup an order by its technical ID.
	 *
	 * @param id order ID
	 * @return Optional OrderResponseDto
	 */
	Optional<OrderResponseDto> findById(Long id);

	/**
	 * Lookup an order by its business code.
	 *
	 * @param code order code
	 * @return Optional OrderResponseDto
	 */
	Optional<OrderResponseDto> findByCode(String code);
}