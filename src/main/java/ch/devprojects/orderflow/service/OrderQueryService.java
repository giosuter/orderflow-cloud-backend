package ch.devprojects.orderflow.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import ch.devprojects.orderflow.dto.OrderResponseDto;
import ch.devprojects.orderflow.dto.OrdersPageResponse;

/**
 * Read-only query service for Orders.
 *
 * For the first step, this service uses an in-memory list of stub data. This
 * allows us to: - design and stabilize the API contract, - implement the
 * Angular Orders List screen, - demonstrate filtering and pagination, before we
 * hook this into the real database.
 *
 * Later we can replace the internal implementation with: - a JPA repository
 * query, - or a dedicated query component, without changing the public method
 * signature.
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
	 * @param status   optional order status filter (case-insensitive, e.g. "NEW")
	 * @param customer optional customer name substring to search for
	 *                 (case-insensitive)
	 * @param page     0-based page index
	 * @param size     page size (number of elements per page)
	 * @return a page wrapper containing the matching orders slice and paging
	 *         metadata
	 */
	public OrdersPageResponse findOrders(String status, String customer, int page, int size) {
		if (size <= 0) {
			// Defensive default: avoid division by zero and weird values
			size = 20;
		}
		if (page < 0) {
			page = 0;
		}

		// Normalize filters for case-insensitive comparison
		String normalizedStatus = status != null ? status.trim().toUpperCase(Locale.ROOT) : null;
		String normalizedCustomer = customer != null ? customer.trim().toLowerCase(Locale.ROOT) : null;

		// 1) Filter in memory (later this logic will move to DB-level)
		List<OrderResponseDto> filtered = new ArrayList<>();
		for (OrderResponseDto order : sampleOrders) {
			if (normalizedStatus != null && !normalizedStatus.isEmpty()) {
				if (order.getStatus() == null || !order.getStatus().toUpperCase(Locale.ROOT).equals(normalizedStatus)) {
					continue;
				}
			}

			if (normalizedCustomer != null && !normalizedCustomer.isEmpty()) {
				String customerName = order.getCustomerName() != null ? order.getCustomerName().toLowerCase(Locale.ROOT)
						: "";
				if (!customerName.contains(normalizedCustomer)) {
					continue;
				}
			}

			filtered.add(order);
		}

		long totalElements = filtered.size();
		int totalPages = (int) ((totalElements + size - 1) / size); // ceiling division

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

	/**
	 * Build a small, but realistic, set of sample orders with: - different statuses
	 * - different customers - different assignees - different totals and timestamps
	 *
	 * This is purely for the first development phase. Later we will replace this
	 * with real data from the database.
	 */
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

		// You can add a few more if you want to have more pages to test

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