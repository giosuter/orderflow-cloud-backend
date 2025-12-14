package ch.devprojects.orderflow.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderResponseDto;
import ch.devprojects.orderflow.dto.OrdersPageResponse;

/**
 * Read-only query service for Orders.
 *
 * Current implementation uses an in-memory list of stub data to stabilize: -
 * the API contract - filtering behavior - pagination behavior
 *
 * Later, we can replace the internals with a JPA query without changing the
 * method signature.
 */
@Service
public class OrderQueryService {

	private final List<OrderResponseDto> sampleOrders;

	public OrderQueryService() {
		this.sampleOrders = buildSampleOrders();
	}

	/**
	 * Return a filtered and paginated view of orders.
	 *
	 * Contract: - "customer" is a free-text term that matches BOTH: - order code
	 * (e.g. ORD-2025-0001) - customerName (e.g. Acme GmbH) - "status" is an
	 * optional enum filter
	 *
	 * @param customerTerm optional search term (matches code OR customerName)
	 * @param status       optional status filter
	 * @param page         0-based page index
	 * @param size         page size
	 * @return OrdersPageResponse containing page content + metadata
	 */
	public OrdersPageResponse findOrders(String customerTerm, OrderStatus status, int page, int size) {

		// Defensive defaults
		if (size <= 0) {
			size = 20;
		}
		if (page < 0) {
			page = 0;
		}

		final String normalizedTerm = (customerTerm != null && !customerTerm.trim().isEmpty())
				? customerTerm.trim().toLowerCase(Locale.ROOT)
				: null;

		final String normalizedStatus = (status != null) ? status.name() : null;

		// 1) Filter in memory (later move to DB-level)
		List<OrderResponseDto> filtered = new ArrayList<>();
		for (OrderResponseDto order : sampleOrders) {

			// Status filter
			if (normalizedStatus != null) {
				String orderStatus = order.getStatus();
				if (orderStatus == null || !orderStatus.equalsIgnoreCase(normalizedStatus)) {
					continue;
				}
			}

			// Term filter (code OR customerName)
			if (normalizedTerm != null) {
				String code = order.getCode() != null ? order.getCode().toLowerCase(Locale.ROOT) : "";
				String customerName = order.getCustomerName() != null ? order.getCustomerName().toLowerCase(Locale.ROOT)
						: "";

				if (!code.contains(normalizedTerm) && !customerName.contains(normalizedTerm)) {
					continue;
				}
			}

			filtered.add(order);
		}

		long totalElements = filtered.size();
		int totalPages = (int) ((totalElements + size - 1) / size);

		// 2) Compute page slice
		int fromIndex = page * size;

		List<OrderResponseDto> pageContent;
		if (fromIndex >= filtered.size()) {
			pageContent = Collections.emptyList();
		} else {
			int toIndex = Math.min(fromIndex + size, filtered.size());
			pageContent = filtered.subList(fromIndex, toIndex);
		}

		// 3) Build response
		OrdersPageResponse response = new OrdersPageResponse();
		response.setContent(pageContent);
		response.setPage(page);
		response.setSize(size);
		response.setTotalElements(totalElements);
		response.setTotalPages(totalPages);

		return response;
	}

	private List<OrderResponseDto> buildSampleOrders() {
		List<OrderResponseDto> list = new ArrayList<>();

		list.add(buildOrder(1L, "ORD-2025-0001", "NEW", "Acme GmbH", "Giovanni Suter", new BigDecimal("120.50"),
				LocalDateTime.now().minusDays(2)));

		list.add(buildOrder(2L, "ORD-2025-0002", "PROCESSING", "Globex AG", "Anna Keller", new BigDecimal("89.90"),
				LocalDateTime.now().minusDays(1)));

		list.add(buildOrder(3L, "ORD-2025-0003", "PAID", "Innotech Solutions", "Mark Weber", new BigDecimal("240.00"),
				LocalDateTime.now().minusHours(12)));

		list.add(buildOrder(4L, "ORD-2025-0004", "SHIPPED", "Acme GmbH", "Logistics Team", new BigDecimal("560.75"),
				LocalDateTime.now().minusHours(6)));

		list.add(buildOrder(5L, "ORD-2025-0005", "CANCELLED", "Sunrise Retail", "Giovanni Suter",
				new BigDecimal("45.00"), LocalDateTime.now().minusDays(5)));

		list.add(buildOrder(6L, "ORD-2025-0006", "NEW", "Techify AG", "Anna Keller", new BigDecimal("310.25"),
				LocalDateTime.now().minusDays(3)));

		list.add(buildOrder(7L, "ORD-2025-0007", "PROCESSING", "BlueOcean GmbH", "Support Team",
				new BigDecimal("199.99"), LocalDateTime.now().minusDays(4)));

		list.add(buildOrder(8L, "ORD-2025-0008", "PAID", "Acme GmbH", "Mark Weber", new BigDecimal("870.00"),
				LocalDateTime.now().minusDays(7)));

		list.add(buildOrder(9L, "ORD-2025-0009", "SHIPPED", "FutureLab AG", "Logistics Team", new BigDecimal("42.10"),
				LocalDateTime.now().minusHours(2)));

		list.add(buildOrder(10L, "ORD-2025-0010", "NEW", "Globex AG", "Giovanni Suter", new BigDecimal("15.75"),
				LocalDateTime.now().minusHours(1)));

		return list;
	}

	private OrderResponseDto buildOrder(Long id, String code, String status, String customerName, String assignedTo,
			BigDecimal total, LocalDateTime createdAt) {

		OrderResponseDto dto = new OrderResponseDto();
		dto.setId(id);
		dto.setCode(code);
		dto.setStatus(status);
		dto.setCustomerName(customerName);
		dto.setAssignedTo(assignedTo);
		dto.setTotal(total);
		dto.setCreatedAt(createdAt);
		return dto;
	}
}