package ch.devprojects.orderflow.service;

import ch.devprojects.orderflow.dto.OrderDto;

import java.util.List;

public interface OrderService {

	OrderDto create(OrderDto dto);
	OrderDto update(Long id, OrderDto dto);
	List<OrderDto> findAll();
	OrderDto findById(Long id);
	void delete(Long id);
}