package ch.devprojects.orderflow.mapper;

import java.time.Instant;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.dto.OrderDto;

/**
 * Manual mapper between JPA entity and API DTO.
 *
 * This version maps ALL relevant fields including customerName.
 * - toDto: entity -> dto (for responses)
 * - toEntity: dto -> new entity (for create)
 * - updateEntityFromDto: dto -> existing entity (for update)
 */
public class OrderMapper {

    public OrderDto toDto(Order entity) {
        if (entity == null) {
            return null;
        }

        OrderDto dto = new OrderDto();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setStatus(entity.getStatus());
        dto.setTotal(entity.getTotal());
        dto.setCustomerName(entity.getCustomerName());   // <-- NEW
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public Order toEntity(OrderDto dto) {
        if (dto == null) {
            return null;
        }

        Order entity = new Order();
        entity.setCode(dto.getCode());
        entity.setStatus(dto.getStatus());
        entity.setTotal(dto.getTotal());
        entity.setCustomerName(dto.getCustomerName());   // <-- NEW

        if (dto.getCreatedAt() != null) {
            entity.setCreatedAt(dto.getCreatedAt());
        }
        if (dto.getUpdatedAt() != null) {
            entity.setUpdatedAt(dto.getUpdatedAt());
        }

        return entity;
    }

    public void updateEntityFromDto(OrderDto dto, Order entity) {
        if (dto == null || entity == null) {
            return;
        }

        boolean changed = false;

        if (dto.getCode() != null && !dto.getCode().equals(entity.getCode())) {
            entity.setCode(dto.getCode());
            changed = true;
        }

        if (dto.getStatus() != null && dto.getStatus() != entity.getStatus()) {
            entity.setStatus(dto.getStatus());
            changed = true;
        }

        if (dto.getTotal() != null &&
                (entity.getTotal() == null || dto.getTotal().compareTo(entity.getTotal()) != 0)) {
            entity.setTotal(dto.getTotal());
            changed = true;
        }

        if (dto.getCustomerName() != null &&
                (entity.getCustomerName() == null || !dto.getCustomerName().equals(entity.getCustomerName()))) {
            entity.setCustomerName(dto.getCustomerName());
            changed = true;
        }

        if (dto.getCreatedAt() != null && !dto.getCreatedAt().equals(entity.getCreatedAt())) {
            entity.setCreatedAt(dto.getCreatedAt());
            changed = true;
        }

        if (changed) {
            entity.setUpdatedAt(Instant.now());
        }
    }
}