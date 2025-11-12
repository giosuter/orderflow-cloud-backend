package ch.devprojects.orderflow.service;

import java.util.List;

import ch.devprojects.orderflow.dto.OrderDto;

public interface OrderService {

    OrderDto create(OrderDto dto);

    OrderDto findById(Long id);

    /**
     * Returns all orders as a simple list (no pagination).
     */
    List<OrderDto> findAll();

    OrderDto update(Long id, OrderDto dto);

    void delete(Long id);
}