package ch.devprojects.orderflow.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderResponseDto;
import ch.devprojects.orderflow.dto.OrdersPageResponse;
import ch.devprojects.orderflow.repository.OrderRepository;

/**
 * Read-only query service for Orders.
 *
 * Controller contract: - OrderQueryController expects OrdersPageResponse
 * (DTO-based). - This service builds specifications, executes paged queries,
 * and maps entities to DTOs.
 */
@Service
@Transactional(readOnly = true)
public class OrderQueryService {

	private final OrderRepository orderRepository;

	public OrderQueryService(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	public List<Order> findAll() {
		return orderRepository.findAll();
	}

	public Optional<Order> findById(long id) {
		return orderRepository.findById(id);
	}

	public List<Order> findByStatus(OrderStatus status) {
		return orderRepository.findByStatus(status);
	}

	/**
	 * Search endpoint used by Angular: GET
	 * /api/orders/search?customer=&status=&page=&size=
	 *
	 * "customer" currently matches code OR customerName (contains, ignore case).
	 */
	public OrdersPageResponse findOrders(String customer, OrderStatus status, int page, int size) {

		Pageable pageable = PageRequest.of(Math.max(0, page), normalizeSize(size), defaultSort());

		Specification<Order> spec = Specification.where(null);

		if (customer != null && !customer.trim().isEmpty()) {
			spec = spec.and(matchesCustomerTerm(customer.trim()));
		}

		if (status != null) {
			spec = spec.and(hasStatus(status));
		}

		Page<Order> resultPage = orderRepository.findAll(spec, pageable);

		List<OrderResponseDto> dtos = resultPage.getContent().stream().map(this::toOrderResponseDto)
				.collect(Collectors.toList());

		return new OrdersPageResponse(dtos, resultPage.getNumber(), resultPage.getSize(), resultPage.getTotalElements(),
				resultPage.getTotalPages(), resultPage.isFirst(), resultPage.isLast());
	}

	// ---------------------------------------------------------------------
	// Mapping
	// ---------------------------------------------------------------------

	/**
	 * Maps Order entity -> OrderResponseDto.
	 *
	 * Adjust the field mapping here if your OrderResponseDto uses different
	 * property names.
	 */
	private OrderResponseDto toOrderResponseDto(Order order) {
		OrderResponseDto dto = new OrderResponseDto();

		dto.setId(order.getId());
		dto.setCode(order.getCode());

		// OrderStatus (enum) -> String
		if (order.getStatus() != null) {
			dto.setStatus(order.getStatus().name());
		}

		dto.setCustomerName(order.getCustomerName());
		dto.setTotal(order.getTotal());

		// Instant -> LocalDateTime (UTC)
		if (order.getCreatedAt() != null) {
			dto.setCreatedAt(LocalDateTime.ofInstant(order.getCreatedAt(), ZoneOffset.UTC));
		}

		return dto;
	}

	// ---------------------------------------------------------------------
	// Specifications
	// ---------------------------------------------------------------------

	private Specification<Order> matchesCustomerTerm(String term) {
		return (root, query, cb) -> {
			String like = "%" + term.toLowerCase() + "%";
			return cb.or(cb.like(cb.lower(root.get("code")), like), cb.like(cb.lower(root.get("customerName")), like));
		};
	}

	private Specification<Order> hasStatus(OrderStatus status) {
		return (root, query, cb) -> cb.equal(root.get("status"), status);
	}

	// ---------------------------------------------------------------------
	// Helpers
	// ---------------------------------------------------------------------

	private int normalizeSize(int size) {
		if (size <= 0) {
			return 20;
		}
		return Math.min(size, 200);
	}

	private Sort defaultSort() {
		// If your entity does not have "createdAt", change to
		// Sort.by("id").descending()
		return Sort.by(Sort.Direction.DESC, "createdAt");
	}
}