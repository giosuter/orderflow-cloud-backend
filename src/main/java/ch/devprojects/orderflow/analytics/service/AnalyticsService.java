package ch.devprojects.orderflow.analytics.service;

import ch.devprojects.orderflow.analytics.dto.AnalyticsOverviewDto;

/**
 * Service interface for analytics-related operations in OrderFlow Cloud.
 *
 * Responsibilities: - Provide a high-level overview of the current order
 * situation (total count, open/completed/cancelled, total revenue, etc.). -
 * Later we can extend this interface with methods for time-based statistics,
 * trends, charts, etc.
 *
 * Important: - The implementation will be responsible for querying the database
 * (via OrderRepository) and mapping the raw data into the
 * {@link AnalyticsOverviewDto}. - Controllers should stay thin and delegate to
 * this service.
 */
public interface AnalyticsService {

	/**
	 * Compute an overview of all orders in the system.
	 *
	 * This method is intentionally simple for the first version: - counts all
	 * orders - groups them into conceptual buckets (open/completed/cancelled) -
	 * calculates total revenue and average order value
	 *
	 * Later we can add overloaded methods with date filters, status filters,
	 * customer filters, etc.
	 *
	 * @return an {@link AnalyticsOverviewDto} populated with metrics derived from
	 *         all orders in the database.
	 */
	AnalyticsOverviewDto getOverview();
}