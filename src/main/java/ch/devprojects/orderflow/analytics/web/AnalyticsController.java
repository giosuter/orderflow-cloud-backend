package ch.devprojects.orderflow.analytics.web;

import ch.devprojects.orderflow.analytics.dto.AnalyticsOverviewDto;
import ch.devprojects.orderflow.analytics.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing high-level analytics for OrderFlow Cloud.
 *
 * Base path: /api/analytics
 *
 * Endpoints: GET /api/analytics/overview -> returns aggregated information such
 * as: - total number of orders - open / completed / cancelled counts - total
 * revenue - average order value - generatedAt timestamp
 *
 * NOTE: - This controller is intentionally thin. All business logic is
 * implemented in {@link AnalyticsService}.
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

	private final AnalyticsService analyticsService;

	/**
	 * Constructor-based dependency injection.
	 *
	 * We avoid Lombok here to keep the code explicit and easy to understand.
	 */
	public AnalyticsController(AnalyticsService analyticsService) {
		this.analyticsService = analyticsService;
	}

	/**
	 * GET /api/analytics/overview
	 *
	 * Returns the current analytics overview as a JSON DTO.
	 */
	@GetMapping("/overview")
	public ResponseEntity<AnalyticsOverviewDto> getOverview() {
		AnalyticsOverviewDto overview = analyticsService.getOverview();
		return ResponseEntity.ok(overview);
	}
}