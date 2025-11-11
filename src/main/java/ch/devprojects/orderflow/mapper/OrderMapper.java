package ch.devprojects.orderflow.mapper;

import org.springframework.stereotype.Component;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.dto.OrderDto;

/*
 * DTO uses strings for frontend compatibility.
 * Entity uses enums for safer persistence.
 */

@Component
public class OrderMapper {

	// ------------------------------
	// Convert Entity → DTO
	// ------------------------------
	public OrderDto toDto(Order entity) {
		if (entity == null)
			return null;

		OrderDto dto = new OrderDto();
		dto.setId(entity.getId());
		dto.setCode(entity.getCode());
		dto.setStatus(entity.getStatus().name());
		dto.setCreatedAt(entity.getCreatedAt());
		dto.setUpdatedAt(entity.getUpdatedAt());

		return dto;
	}

	// ------------------------------
	// Convert DTO → Entity
	// ------------------------------
	public Order toEntity(OrderDto dto) {
		if (dto == null)
			return null;

		Order entity = new Order();
		entity.setCode(dto.getCode());

		if (dto.getStatus() != null) {
			entity.setStatus(Order.Status.valueOf(dto.getStatus()));
		}

		// createdAt and updatedAt will be set by JPA (if using @PrePersist /
		// @PreUpdate)

		return entity;
	}

	// ------------------------------
	// Update existing entity from DTO
	// ------------------------------
	public void updateEntity(Order entity, OrderDto dto) {
		if (dto.getCode() != null) {
			entity.setCode(dto.getCode());
		}

		if (dto.getStatus() != null) {
			entity.setStatus(Order.Status.valueOf(dto.getStatus()));
		}
	}
}