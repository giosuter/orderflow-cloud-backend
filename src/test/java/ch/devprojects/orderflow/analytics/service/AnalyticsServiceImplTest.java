package ch.devprojects.orderflow.analytics.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ch.devprojects.orderflow.analytics.dto.AnalyticsOverviewDto;
import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.repository.OrderRepository;

/**
 * Unit tests for {@link AnalyticsServiceImpl}.
 *
 * Goal: - Verify that the service correctly aggregates order data into
 * {@link AnalyticsOverviewDto} based on the list of orders returned by
 * {@link OrderRepository}.
 *
 * NOTE: - We use pure unit tests with Mockito (no Spring context).
 */
@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

	@Mock
	private OrderRepository orderRepository;

	@InjectMocks
	private AnalyticsServiceImpl analyticsService;

	@Test
	@DisplayName("getOverview should compute totals and counts correctly")
	void getOverview_shouldComputeAggregatesFromOrders() {
		// ---------------------------------------------------------------------
		// Arrange: build a small in-memory dataset of orders
		// ---------------------------------------------------------------------
		Order o1 = new Order();
		o1.setId(1L);
		o1.setCode("ORD-001");
		o1.setStatus(OrderStatus.NEW);
		o1.setTotal(new BigDecimal("100.00"));

		Order o2 = new Order();
		o2.setId(2L);
		o2.setCode("ORD-002");
		o2.setStatus(OrderStatus.COMPLETED);
		o2.setTotal(new BigDecimal("250.50"));

		Order o3 = new Order();
		o3.setId(3L);
		o3.setCode("ORD-003");
		o3.setStatus(OrderStatus.CANCELLED);
		o3.setTotal(new BigDecimal("0.00"));

		List<Order> orders = Arrays.asList(o1, o2, o3);

		// Repository returns our in-memory orders
		when(orderRepository.findAll()).thenReturn(orders);

		// ---------------------------------------------------------------------
		// Act: call the service
		// ---------------------------------------------------------------------
		AnalyticsOverviewDto overview = analyticsService.getOverview();

		// ---------------------------------------------------------------------
		// Assert: verify aggregate values
		// ---------------------------------------------------------------------
		assertNotNull(overview, "Overview DTO must not be null");

		// Total number of orders
		assertEquals(3L, overview.getTotalOrders(), "Total orders should be 3");

		// Open orders: we treat NEW (and optionally OPEN) as "open"
		long expectedOpen = orders.stream()
				.filter(o -> o.getStatus() == OrderStatus.NEW || o.getStatus() == OrderStatus.OPEN).count();
		assertEquals(expectedOpen, overview.getOpenOrders(), "Open orders mismatch");

		// Completed orders
		long expectedCompleted = orders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED).count();
		assertEquals(expectedCompleted, overview.getCompletedOrders(), "Completed orders mismatch");

		// Cancelled orders
		long expectedCancelled = orders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count();
		assertEquals(expectedCancelled, overview.getCancelledOrders(), "Cancelled orders mismatch");

		// Total revenue = sum of non-null totals
		BigDecimal expectedRevenue = orders.stream().map(Order::getTotal).filter(total -> total != null)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		assertEquals(0, expectedRevenue.compareTo(overview.getTotalRevenue()), "Total revenue mismatch");

		// Average order value:
		// we expect (100.00 + 250.50 + 0.00) / 3 = 116.83 (rounded HALF_UP to 2
		// decimals)
		BigDecimal expectedAverage = expectedRevenue.divide(BigDecimal.valueOf(orders.size()), 2,
				java.math.RoundingMode.HALF_UP);

		assertEquals(0, expectedAverage.compareTo(overview.getAverageOrderValue()), "Average order value mismatch");

		// generatedAt should be set to a non-null Instant (we don't assert exact time)
		assertNotNull(overview.getGeneratedAt(), "generatedAt should not be null");
	}

	@Test
	@DisplayName("getOverview should handle empty order list gracefully")
	void getOverview_shouldHandleEmptyList() {
		// Arrange: repository returns an empty list
		when(orderRepository.findAll()).thenReturn(List.of());

		// Act
		AnalyticsOverviewDto overview = analyticsService.getOverview();

		// Assert: all numeric values should be 0, and generatedAt must be set
		assertNotNull(overview, "Overview DTO must not be null");
		assertEquals(0L, overview.getTotalOrders(), "Total orders should be 0");
		assertEquals(0L, overview.getOpenOrders(), "Open orders should be 0");
		assertEquals(0L, overview.getCompletedOrders(), "Completed orders should be 0");
		assertEquals(0L, overview.getCancelledOrders(), "Cancelled orders should be 0");

		assertEquals(0, overview.getTotalRevenue().compareTo(BigDecimal.ZERO), "Total revenue should be 0");
		assertEquals(0, overview.getAverageOrderValue().compareTo(BigDecimal.ZERO), "Average order value should be 0");

		assertNotNull(overview.getGeneratedAt(), "generatedAt should not be null even for empty list");
	}
}