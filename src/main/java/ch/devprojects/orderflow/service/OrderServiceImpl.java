package ch.devprojects.orderflow.service;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.mapper.OrderMapper;
import ch.devprojects.orderflow.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	private final OrderRepository repo;
	private final OrderMapper mapper;

	public OrderServiceImpl(OrderRepository repo, OrderMapper mapper) {
		this.repo = repo;
		this.mapper = mapper;
	}

	@Override
	public OrderDto create(OrderDto dto) {
		Order entity = mapper.toEntity(dto);
		repo.save(entity);
		return mapper.toDto(entity);
	}

	@Override
	public OrderDto update(Long id, OrderDto dto) {
		Order entity = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));

		mapper.updateEntity(entity, dto);
		repo.save(entity);

		return mapper.toDto(entity);
	}

	@Override
	public List<OrderDto> findAll() {
		return repo.findAll().stream().map(mapper::toDto).toList();
	}

	@Override
	public OrderDto findById(Long id) {
		Order entity = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
		return mapper.toDto(entity);
	}

	@Override
	public void delete(Long id) {
		if (!repo.existsById(id)) {
			throw new EntityNotFoundException("Order not found: " + id);
		}
		repo.deleteById(id);
	}
}