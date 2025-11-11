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

	public OrderDto toDto(Order entity) {
		if (entity == null)
			return null;
		OrderDto dto = new OrderDto();
		dto.setId(entity.getId());
		dto.setCode(entity.getCode());
		dto.setStatus(entity.getStatus().name());
		dto.setTotal(entity.getTotal()); // <<— map total
		dto.setCreatedAt(entity.getCreatedAt());
		dto.setUpdatedAt(entity.getUpdatedAt());
		return dto;
	}

	public Order toEntity(OrderDto dto) {
		if (dto == null)
			return null;
		Order entity = new Order();
		entity.setCode(dto.getCode());
		if (dto.getStatus() != null) {
			entity.setStatus(Order.Status.valueOf(dto.getStatus()));
		}
		entity.setTotal(dto.getTotal()); // <<— map total
		return entity;
	}

	public void updateEntity(Order entity, OrderDto dto) {
		if (dto.getCode() != null) {
			entity.setCode(dto.getCode());
		}
		if (dto.getStatus() != null) {
			entity.setStatus(Order.Status.valueOf(dto.getStatus()));
		}
		if (dto.getTotal() != null) {
			entity.setTotal(dto.getTotal()); // <<— map total
		}
	}
}