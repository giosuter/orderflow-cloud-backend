package ch.devprojects.orderflow.service;

import java.util.List;
import java.util.Objects;

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
 * Default implementation of OrderService.
 *
 * Key detail for this task: - persists and returns "description" (canonical)
 * instead of old "comment"
 *
 * Important: - OrderDto accepts legacy JSON field "comment" via @JsonAlias in
 * DTO, but we always persist into entity.description and DB column
 * orders.description.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;

	public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
		this.orderRepository = Objects.requireNonNull(orderRepository, "orderRepository must not be null");
		this.orderMapper = Objects.requireNonNull(orderMapper, "orderMapper must not be null");
	}

	@Override
	public OrderDto create(OrderDto dto) {
		validateForCreate(dto);

		// Uses mapper method (now defined) and persists description.
		Order entity = orderMapper.toEntityForCreate(dto);
		Order saved = orderRepository.save(entity);
		return orderMapper.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public OrderDto findById(Long id) {
		Order entity = orderRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Order not found, id=" + id));
		return orderMapper.toDto(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderDto> findAll() {
		return orderRepository.findAll().stream().map(orderMapper::toDto).toList();
	}

	@Override
	public OrderDto update(Long id, OrderDto dto) {
		validateForUpdate(dto);

		Order existing = orderRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Order not found, id=" + id));

		// Uses mapper method (now defined) and persists description.
		orderMapper.applyToExistingEntityForUpdate(dto, existing);

		Order saved = orderRepository.save(existing);
		return orderMapper.toDto(saved);
	}

	@Override
	public void delete(Long id) {
		if (!orderRepository.existsById(id)) {
			throw new EntityNotFoundException("Order not found, id=" + id);
		}
		orderRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderDto> search(String code, OrderStatus status) {
		Specification<Order> spec = Specification.where(null);

		if (code != null && !code.trim().isEmpty()) {
			String codeLike = "%" + code.trim().toLowerCase() + "%";
			spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("code")), codeLike));
		}

		if (status != null) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
		}

		return orderRepository.findAll(spec).stream().map(orderMapper::toDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public OrderDto findByCode(String code) {
		if (code == null || code.trim().isEmpty()) {
			throw new IllegalArgumentException("code must not be blank");
		}

		Order entity = orderRepository.findByCode(code.trim())
				.orElseThrow(() -> new EntityNotFoundException("Order not found, code=" + code.trim()));

		return orderMapper.toDto(entity);
	}

	private void validateForCreate(OrderDto dto) {
		if (dto == null) {
			throw new IllegalArgumentException("OrderDto must not be null");
		}
		if (dto.getCode() == null || dto.getCode().trim().isEmpty()) {
			throw new IllegalArgumentException("code must not be blank");
		}
		if (dto.getTotal() == null) {
			throw new IllegalArgumentException("total must not be null");
		}
		if (dto.getTotal().signum() <= 0) {
			throw new IllegalArgumentException("total must be > 0");
		}
		// status may be null; mapper defaults it to NEW
		// description is optional; no validation needed
	}

	private void validateForUpdate(OrderDto dto) {
		// For now, same validation as create.
		validateForCreate(dto);
	}
}