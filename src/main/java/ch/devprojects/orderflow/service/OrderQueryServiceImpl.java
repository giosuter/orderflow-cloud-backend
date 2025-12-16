package ch.devprojects.orderflow.service;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.dto.OrdersPageResponse;
import ch.devprojects.orderflow.repository.OrderRepository;

/**
 * Query service implementation based on JPA Specifications.
 *
 * Key detail for this task: - The returned DTO includes "description" (mapped
 * in toDto()).
 */
@Service
public class OrderQueryServiceImpl implements OrderQueryService {

	private final OrderRepository orderRepository;

	/**
	 * Restrict sorting to known-safe fields to avoid invalid property access. Add
	 * more fields here when needed.
	 */
	private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "updatedAt", "code", "total", "status",
			"customerName");

	public OrderQueryServiceImpl(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Override
	public Page<OrderDto> search(String customer, OrderStatus status, String codeFrom, String codeTo,
			BigDecimal totalMin, BigDecimal totalMax, Pageable pageable) {

		Specification<Order> spec = buildSpec(customer, status, codeFrom, codeTo, totalMin, totalMax);
		return orderRepository.findAll(spec, pageable).map(this::toDto);
	}

	@Override
	public OrdersPageResponse findOrders(String customer, OrderStatus status, int page, int size, String sortBy,
			String sortDir, BigDecimal totalMin, BigDecimal totalMax) {

		int safePage = Math.max(0, page);
		int safeSize = Math.max(1, size);

		String safeSortBy = (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy.trim();
		if (!ALLOWED_SORT_FIELDS.contains(safeSortBy)) {
			// Fallback to a stable default to avoid runtime errors
			safeSortBy = "createdAt";
		}

		Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
		PageRequest pageable = PageRequest.of(safePage, safeSize, Sort.by(direction, safeSortBy));

		Specification<Order> spec = buildSpec(customer, status, null, null, totalMin, totalMax);
		Page<Order> result = orderRepository.findAll(spec, pageable);

		OrdersPageResponse response = new OrdersPageResponse();
		response.setContent(result.getContent().stream().map(this::toDto).toList());
		response.setPage(result.getNumber());
		response.setSize(result.getSize());
		response.setTotalElements(result.getTotalElements());
		response.setTotalPages(result.getTotalPages());
		return response;
	}

	@Override
	public OrdersPageResponse findOrders(String customer, OrderStatus status, int page, int size) {
		// Default sorting and no total filters
		return findOrders(customer, status, page, size, "createdAt", "desc", null, null);
	}

	private Specification<Order> buildSpec(String customer, OrderStatus status, String codeFrom, String codeTo,
			BigDecimal totalMin, BigDecimal totalMax) {

		Specification<Order> spec = Specification.where(null);

		if (customer != null && !customer.trim().isEmpty()) {
			String customerLike = "%" + customer.trim().toLowerCase() + "%";
			spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("customerName")), customerLike));
		}

		if (status != null) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
		}

		// Optional code range filters (if your codes are comparable strings; adjust if
		// needed)
		if (codeFrom != null && !codeFrom.trim().isEmpty()) {
			String from = codeFrom.trim();
			spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("code"), from));
		}

		if (codeTo != null && !codeTo.trim().isEmpty()) {
			String to = codeTo.trim();
			spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("code"), to));
		}

		if (totalMin != null) {
			spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("total"), totalMin));
		}

		if (totalMax != null) {
			spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("total"), totalMax));
		}

		return spec;
	}

	private OrderDto toDto(Order order) {
		OrderDto dto = new OrderDto();
		dto.setId(order.getId());
		dto.setCode(order.getCode());
		dto.setCustomerName(order.getCustomerName());
		dto.setTotal(order.getTotal());
		dto.setCreatedAt(order.getCreatedAt());
		dto.setUpdatedAt(order.getUpdatedAt());

		// Entity enum -> DTO String
		dto.setStatus(order.getStatus() == null ? null : order.getStatus().name());

		// Critical change: comment -> description
		dto.setDescription(order.getDescription());

		return dto;
	}
}