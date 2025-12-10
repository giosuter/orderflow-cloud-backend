package ch.devprojects.orderflow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.devprojects.orderflow.dto.OrdersPageResponse;

/**
 * Small unit tests for OrderQueryService.
 *
 * These tests verify: - basic paging behavior, - that filtering by status and
 * customer works.
 *
 * Later, when we switch to a database-backed implementation, this test will
 * help ensure we do not accidentally change the semantics.
 */
class OrderQueryServiceTest {

	private final OrderQueryService service = new OrderQueryService();

	@Test
	@DisplayName("findOrders without filters returns first page of data")
	void findOrders_noFilters_returnsFirstPage() {
		OrdersPageResponse page = service.findOrders(null, null, 0, 5);

		// We expect some content (we added multiple sample orders)
		assertFalse(page.getContent().isEmpty(), "Content should not be empty for default sample data");
		assertEquals(0, page.getPage(), "Page index should be 0");
		assertEquals(5, page.getSize(), "Page size should be 5");
	}

	@Test
	@DisplayName("findOrders filters by status (NEW)")
	void findOrders_filtersByStatus() {
		OrdersPageResponse page = service.findOrders("NEW", null, 0, 20);

		// All returned orders must have status NEW (case-insensitive)
		page.getContent().forEach(order -> assertEquals("NEW", order.getStatus(), "All orders should have status NEW"));
	}

	@Test
	@DisplayName("findOrders filters by customer substring")
	void findOrders_filtersByCustomerSubstring() {
		OrdersPageResponse page = service.findOrders(null, "acme", 0, 20);

		// All returned orders must contain "acme" (case-insensitive) in customerName
		page.getContent()
				.forEach(order -> assertFalse(
						order.getCustomerName() == null || !order.getCustomerName().toLowerCase().contains("acme"),
						"Customer name should contain 'acme' (case-insensitive)"));
	}
}