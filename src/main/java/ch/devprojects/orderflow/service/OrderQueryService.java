package ch.devprojects.orderflow.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderResponseDto;
import ch.devprojects.orderflow.dto.OrdersPageResponse;
import ch.devprojects.orderflow.repository.OrderRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Read-only query service for Orders.
 *
 * Responsibilities: - Build Specifications for optional filters - Apply
 * server-side pagination and sorting - Map entities to DTOs for the API
 * response contract
 */
@Service
@Transactional(readOnly = true)
public class OrderQueryService {

	private static final int DEFAULT_SIZE = 20;
	private static final int MAX_SIZE = 200;

	/**
	 * Whitelist of allowed sort fields to prevent exposing arbitrary JPA fields.
	 *
	 * These names must match actual Order entity field names.
	 */
	private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "code", "customerName", "total",
			"status");

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
	 * /api/orders/search?customer=&status=&page=&size=&sortBy=&sortDir=&totalMin=&totalMax=
	 *
	 * Filters: - customer: contains match against code OR customerName (ignore
	 * case) - status: exact match on enum - totalMin/totalMax: inclusive bounds for
	 * Order.total
	 *
	 * Sorting: - sortBy must be in ALLOWED_SORT_FIELDS; otherwise defaults to
	 * createdAt - sortDir is asc/desc; otherwise defaults to desc
	 */
	public OrdersPageResponse findOrders(String customer, OrderStatus status, int page, int size, String sortBy,
			String sortDir, BigDecimal totalMin, BigDecimal totalMax) {

		Sort sort = buildSort(sortBy, sortDir);

		Pageable pageable = PageRequest.of(Math.max(0, page), normalizeSize(size), sort);

		Specification<Order> spec = Specification.where(null);

		if (customer != null && !customer.trim().isEmpty()) {
			spec = spec.and(matchesCustomerTerm(customer.trim()));
		}

		if (status != null) {
			spec = spec.and(hasStatus(status));
		}

		if (totalMin != null) {
			spec = spec.and(totalGreaterOrEqual(totalMin));
		}

		if (totalMax != null) {
			spec = spec.and(totalLessOrEqual(totalMax));
		}

		Page<Order> resultPage = orderRepository.findAll(spec, pageable);

		List<OrderResponseDto> dtos = resultPage.getContent().stream().map(this::toOrderResponseDto)
				.collect(Collectors.toList());

		return new OrdersPageResponse(dtos, resultPage.getNumber(), resultPage.getSize(), resultPage.getTotalElements(),
				resultPage.getTotalPages(), resultPage.isFirst(), resultPage.isLast());
	}

	// ---------------------------------------------------------------------
	// Mapping: Entity -> DTO
	// ---------------------------------------------------------------------

	/**
	 * Converts Order entity to OrderResponseDto.
	 *
	 * Type conversions: - status: OrderStatus enum -> String - createdAt: Instant
	 * -> LocalDateTime (UTC)
	 */
	private OrderResponseDto toOrderResponseDto(Order order) {
		OrderResponseDto dto = new OrderResponseDto();

		dto.setId(order.getId());
		dto.setCode(order.getCode());

		if (order.getStatus() != null) {
			dto.setStatus(order.getStatus().name());
		}

		dto.setCustomerName(order.getCustomerName());
		dto.setTotal(order.getTotal());

		if (order.getCreatedAt() != null) {
			dto.setCreatedAt(LocalDateTime.ofInstant(order.getCreatedAt(), ZoneOffset.UTC));
		} else {
			dto.setCreatedAt(null);
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

	private Specification<Order> totalGreaterOrEqual(BigDecimal totalMin) {
		return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("total"), totalMin);
	}

	private Specification<Order> totalLessOrEqual(BigDecimal totalMax) {
		return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("total"), totalMax);
	}

	// ---------------------------------------------------------------------
	// Helpers
	// ---------------------------------------------------------------------

	private int normalizeSize(int size) {
		if (size <= 0) {
			return DEFAULT_SIZE;
		}
		return Math.min(size, MAX_SIZE);
	}

	private Sort buildSort(String sortBy, String sortDir) {
		String safeSortBy = (sortBy != null) ? sortBy.trim() : "";
		if (!ALLOWED_SORT_FIELDS.contains(safeSortBy)) {
			safeSortBy = "createdAt";
		}

		Sort.Direction direction = Sort.Direction.DESC;
		if (sortDir != null && "asc".equalsIgnoreCase(sortDir.trim())) {
			direction = Sort.Direction.ASC;
		}

		return Sort.by(direction, safeSortBy);
	}
}