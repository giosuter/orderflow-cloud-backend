package ch.devprojects.orderflow.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.dto.OrderResponseDto;
import ch.devprojects.orderflow.repository.OrderRepository;

/**
 * Default implementation of OrderLookupService.
 *
 * Read-only, no business logic.
 */
@Service
@Transactional(readOnly = true)
public class OrderLookupServiceImpl implements OrderLookupService {

	private final OrderRepository orderRepository;

	public OrderLookupServiceImpl(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Override
	public Optional<OrderResponseDto> findById(Long id) {
		return orderRepository.findById(id).map(this::toResponseDto);
	}

	@Override
	public Optional<OrderResponseDto> findByCode(String code) {
		if (code == null || code.isBlank()) {
			return Optional.empty();
		}
		return orderRepository.findByCode(code.trim()).map(this::toResponseDto);
	}

	private OrderResponseDto toResponseDto(Order order) {
		OrderResponseDto dto = new OrderResponseDto();
		dto.setId(order.getId());
		dto.setCode(order.getCode());
		dto.setCustomerName(order.getCustomerName());
		dto.setTotal(order.getTotal());
		dto.setStatus(order.getStatus() == null ? null : order.getStatus().name());
		dto.setCreatedAt(order.getCreatedAt());
		dto.setUpdatedAt(order.getUpdatedAt());
		return dto;
	}
}