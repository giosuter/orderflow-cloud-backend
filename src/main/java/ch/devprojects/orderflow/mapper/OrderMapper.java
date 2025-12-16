package ch.devprojects.orderflow.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.dto.OrderResponseDto;

/**
 * Manual mapper between the Order JPA entity and DTOs.
 *
 * Naming note (important for your current codebase): - Your existing unit tests
 * expect: - toEntity(OrderDto) - updateEntityFromDto(OrderDto, Order)
 *
 * - Your service layer (as we refactored it) uses more explicit names: -
 * toEntityForCreate(OrderDto) - applyToExistingEntityForUpdate(OrderDto, Order)
 *
 * To keep the project consistent AND avoid breaking tests, this mapper provides
 * BOTH: - The explicit methods (preferred for services) - Compatibility
 * wrappers (delegate to the explicit ones)
 *
 * Key detail for this task: - The canonical free-text field is "description"
 * (replaces old "comment"). - DTO accepts legacy JSON "comment" via @JsonAlias
 * on the DTO field.
 */
@Component
public class OrderMapper {

	/**
	 * Maps an Order entity to an OrderDto (used for list/search).
	 */
	public OrderDto toDto(Order order) {
		if (order == null) {
			return null;
		}

		OrderDto dto = new OrderDto();
		dto.setId(order.getId());
		dto.setCode(order.getCode());
		dto.setCustomerName(order.getCustomerName());
		dto.setTotal(order.getTotal());
		dto.setCreatedAt(order.getCreatedAt());
		dto.setUpdatedAt(order.getUpdatedAt());

		// Entity enum -> DTO string (contract for frontend)
		dto.setStatus(order.getStatus() == null ? null : order.getStatus().name());

		// Critical rename: comment -> description
		dto.setDescription(order.getDescription());

		return dto;
	}

	/**
	 * Maps an Order entity to an OrderResponseDto (used for detail endpoints, if
	 * needed).
	 */
	public OrderResponseDto toResponseDto(Order order) {
		if (order == null) {
			return null;
		}

		OrderResponseDto dto = new OrderResponseDto();
		dto.setId(order.getId());
		dto.setCode(order.getCode());
		dto.setCustomerName(order.getCustomerName());
		dto.setTotal(order.getTotal());
		dto.setCreatedAt(order.getCreatedAt());
		dto.setUpdatedAt(order.getUpdatedAt());
		dto.setStatus(order.getStatus() == null ? null : order.getStatus().name());
		dto.setDescription(order.getDescription());
		return dto;
	}

	/*
	 * -----------------------------------------------------------------------
	 * Preferred explicit methods used by the service layer
	 * ---------------------------------------------------------------------
	 */

	/**
	 * Creates a NEW Order entity for a create operation.
	 *
	 * Notes: - status defaults to NEW if not provided - timestamps are initialized
	 * here for consistent behavior - description is optional, but persisted if
	 * present
	 */
	public Order toEntityForCreate(OrderDto dto) {
		if (dto == null) {
			return null;
		}

		Order entity = new Order();
		entity.setCode(dto.getCode());
		entity.setCustomerName(dto.getCustomerName());
		entity.setTotal(dto.getTotal());

		// default NEW for creates when missing
		entity.setStatus(parseStatus(dto.getStatus(), OrderStatus.NEW));

		// Canonical rename: comment -> description
		entity.setDescription(dto.getDescription());

		Instant now = Instant.now();
		entity.setCreatedAt(now);
		entity.setUpdatedAt(now);

		return entity;
	}

	/**
	 * Updates an EXISTING Order entity for an update operation.
	 *
	 * Notes: - keeps createdAt untouched - updates updatedAt timestamp - if status
	 * is missing, keeps existing status
	 */
	public void applyToExistingEntityForUpdate(OrderDto dto, Order entity) {
		if (dto == null || entity == null) {
			return;
		}

		entity.setCode(dto.getCode());
		entity.setCustomerName(dto.getCustomerName());
		entity.setTotal(dto.getTotal());

		entity.setStatus(parseStatus(dto.getStatus(), entity.getStatus()));

		// Canonical rename: comment -> description
		entity.setDescription(dto.getDescription());

		entity.setUpdatedAt(Instant.now());
	}

	/*
	 * -----------------------------------------------------------------------
	 * Compatibility methods expected by your current unit tests
	 * ---------------------------------------------------------------------
	 */

	/**
	 * Compatibility wrapper for existing tests/code.
	 *
	 * Equivalent to "create a new entity from the DTO". Delegates to
	 * {@link #toEntityForCreate(OrderDto)}.
	 */
	public Order toEntity(OrderDto dto) {
		return toEntityForCreate(dto);
	}

	/**
	 * Compatibility wrapper for existing tests/code.
	 *
	 * Equivalent to "update an existing entity from the DTO". Delegates to
	 * {@link #applyToExistingEntityForUpdate(OrderDto, Order)}.
	 */
	public void updateEntityFromDto(OrderDto dto, Order entity) {
		applyToExistingEntityForUpdate(dto, entity);
	}

	/**
	 * Converts DTO status string (e.g. "paid") to enum (PAID). If null/blank,
	 * returns the provided default.
	 */
	private OrderStatus parseStatus(String status, OrderStatus defaultValue) {
		if (status == null) {
			return defaultValue;
		}
		String s = status.trim();
		if (s.isEmpty()) {
			return defaultValue;
		}
		return OrderStatus.valueOf(s.toUpperCase());
	}
}