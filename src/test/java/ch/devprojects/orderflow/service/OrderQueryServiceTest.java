package ch.devprojects.orderflow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrdersPageResponse;

/**
 * Unit tests for {@link OrderQueryService}.
 *
 * These tests operate on the in-memory sample data to verify that filtering and
 * pagination work as expected.
 */
class OrderQueryServiceTest {

	private final OrderQueryService service = new OrderQueryService();

	@Test
	@DisplayName("findOrders without filters should return first page of sample data")
	void findOrders_withoutFilters_returnsFirstPage() {
		OrdersPageResponse result = service.findOrders(null, null, 0, 20);

		assertFalse(result.getContent().isEmpty(), "Content should not be empty");
		assertEquals(0, result.getPage(), "Page index should be 0");
		// sample data has 10 entries in current implementation
		assertEquals(10, result.getTotalElements(), "Total elements should match sample size");
	}

	@Test
	@DisplayName("findOrders with status filter NEW should only return NEW orders")
	void findOrders_withStatusFilter_filtersByStatus() {
		OrdersPageResponse result = service.findOrders(null, OrderStatus.NEW, 0, 20);

		assertFalse(result.getContent().isEmpty(), "Content should not be empty");
		assertTrue(result.getContent().stream().allMatch(o -> "NEW".equalsIgnoreCase(o.getStatus())),
				"All returned orders must have status NEW");
	}

	@Test
	@DisplayName("findOrders with customer substring filter should match case-insensitively")
	void findOrders_withCustomerFilter_filtersByCustomer() {
		OrdersPageResponse result = service.findOrders("acme", null, 0, 20);

		assertFalse(result.getContent().isEmpty(), "Content should not be empty");
		assertTrue(
				result.getContent().stream().allMatch(
						o -> o.getCustomerName() != null && o.getCustomerName().toLowerCase().contains("acme")),
				"All returned orders must contain 'acme' in the customer name");
	}
}