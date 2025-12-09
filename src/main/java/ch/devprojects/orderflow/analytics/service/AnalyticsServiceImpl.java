package ch.devprojects.orderflow.analytics.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.devprojects.orderflow.analytics.dto.AnalyticsOverviewDto;
import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.repository.OrderRepository;

/**
 * Default implementation of {@link AnalyticsService}.
 *
 * Responsibilities: - Load all orders from the database (first simple version).
 * - Compute basic metrics: - total number of orders - how many are "open",
 * "completed", "cancelled" - total revenue (sum of all order totals) - average
 * order value
 *
 * Implementation notes: - This first version does the aggregation in Java, not
 * in SQL. That is absolutely fine for small datasets and keeps the
 * implementation simple and easy to test. - Later, if needed, we can optimize
 * by adding dedicated repository methods with JPQL/SQL for aggregation.
 *
 * Status buckets: - We group statuses by name to be robust against enum
 * changes. There is no direct dependency on a specific OrderStatus constant.
 *
 * COMPLETED bucket: - "COMPLETED" - "DONE"
 *
 * CANCELLED bucket: - "CANCELLED" - "CANCELED"
 *
 * OPEN bucket: - everything else (including null status)
 */
@Service
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

	private final OrderRepository orderRepository;

	/**
	 * Constructor-based dependency injection. This is preferred over field
	 * injection and works nicely with tests.
	 */
	public AnalyticsServiceImpl(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Override
	public AnalyticsOverviewDto getOverview() {
		// 1) Load all orders – first simple version, no filters.
		final List<Order> orders = orderRepository.findAll();

		final long totalOrders = orders.size();

		long openOrders = 0L;
		long completedOrders = 0L;
		long cancelledOrders = 0L;

		BigDecimal totalRevenue = BigDecimal.ZERO;

		// 2) Walk through all orders and compute metrics.
		for (Order order : orders) {
			if (order == null) {
				continue;
			}

			// ---- Revenue aggregation ----
			if (order.getTotal() != null) {
				totalRevenue = totalRevenue.add(order.getTotal());
			}

			// ---- Status buckets ----
			String statusName = null;
			if (order.getStatus() != null) {
				statusName = order.getStatus().name();
			}

			if (statusName == null) {
				// No status -> treat as "open"
				openOrders++;
			} else if ("COMPLETED".equalsIgnoreCase(statusName) || "DONE".equalsIgnoreCase(statusName)) {
				completedOrders++;
			} else if ("CANCELLED".equalsIgnoreCase(statusName) || "CANCELED".equalsIgnoreCase(statusName)) {
				cancelledOrders++;
			} else {
				// NEW, PAID, IN_PROGRESS, etc. => still considered "open"
				openOrders++;
			}
		}

		// 3) Compute average order value.
		BigDecimal averageOrderValue = BigDecimal.ZERO;
		if (totalOrders > 0 && totalRevenue.signum() != 0) {
			averageOrderValue = totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);
		}

		// 4) Build DTO
		AnalyticsOverviewDto dto = new AnalyticsOverviewDto();
		dto.setTotalOrders(totalOrders);
		dto.setOpenOrders(openOrders);
		dto.setCompletedOrders(completedOrders);
		dto.setCancelledOrders(cancelledOrders);
		dto.setTotalRevenue(totalRevenue);
		dto.setAverageOrderValue(averageOrderValue);
		dto.setGeneratedAt(Instant.now());

		return dto;
	}
}