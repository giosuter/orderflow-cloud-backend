package ch.devprojects.orderflow.service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.mapper.OrderMapper;
import ch.devprojects.orderflow.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;

/**
 * OrderServiceImpl — business/service layer for Orders.
 *
 * Responsibilities:
 * - Keeps controllers thin.
 * - Centralizes validation, defaults, timestamps, and mapping.
 * - Delegates persistence to OrderRepository.
 * - Uses OrderMapper for entity <-> DTO conversion.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    // Simple manual mapper; we keep it as a field so it can be reused.
    private final OrderMapper orderMapper = new OrderMapper();

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // -------------------------------
    // CREATE
    // -------------------------------
    @Override
    public OrderDto create(OrderDto dto) {
        Objects.requireNonNull(dto, "OrderDto must not be null");

        // Minimal guard rails (Bean Validation on the DTO will also run in the controller)
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            throw new IllegalArgumentException("code must not be blank");
        }
        if (dto.getTotal() == null) {
            throw new IllegalArgumentException("total must not be null");
        }

        Order entity = orderMapper.toEntity(dto);

        // Defaults: if status is not provided, use NEW
        if (entity.getStatus() == null) {
            entity.setStatus(OrderStatus.NEW);
        }

        // Initialize timestamps if missing
        Instant now = Instant.now();
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        entity.setUpdatedAt(now);

        Order saved = orderRepository.save(entity);
        return orderMapper.toDto(saved);
    }

    // -------------------------------
    // READ (by id)
    // -------------------------------
    @Override
    @Transactional(readOnly = true)
    public OrderDto findById(Long id) {
        Order found = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
        return orderMapper.toDto(found);
    }

    // -------------------------------
    // READ (all)
    // -------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    // -------------------------------
    // UPDATE (partial, null-safe)
    // -------------------------------
    @Override
    public OrderDto update(Long id, OrderDto dto) {
        Objects.requireNonNull(dto, "OrderDto must not be null");

        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));

        // In-place update, ignoring nulls; updatedAt is set if something changed
        orderMapper.updateEntityFromDto(dto, existing);

        // Ensure status is never null after update
        if (existing.getStatus() == null) {
            existing.setStatus(OrderStatus.NEW);
        }

        Order saved = orderRepository.save(existing);
        return orderMapper.toDto(saved);
    }

    // -------------------------------
    // DELETE
    // -------------------------------
    @Override
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order not found: " + id);
        }
        orderRepository.deleteById(id);
    }

    // -------------------------------
    // SEARCH (by code + status)
    // -------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> search(String code, OrderStatus status) {
        // Start with a "true" specification (matches all).
        Specification<Order> spec = OrderSpecifications.alwaysTrue();

        // Add filter by code (substring, case-insensitive) if provided.
        if (code != null && !code.isBlank()) {
            spec = spec.and(OrderSpecifications.codeContainsIgnoreCase(code));
        }

        // Add filter by status if provided.
        if (status != null) {
            spec = spec.and(OrderSpecifications.statusEquals(status));
        }

        return orderRepository.findAll(spec)
                .stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }
}