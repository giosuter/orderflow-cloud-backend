package ch.devprojects.orderflow.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.devprojects.orderflow.dto.OrdersPageResponse;
import ch.devprojects.orderflow.service.OrderQueryService;

/**
 * REST controller exposing a read-only Orders search endpoint.
 *
 * URL example (local, dev profile): GET
 * http://localhost:8080/orderflow-api/api/orders/search?status=NEW&customer=acme&page=0&size=5
 *
 * URL example (production, Hostpoint): GET
 * https://devprojects.ch/orderflow-api/api/orders/search?status=PAID&customer=globex&page=0&size=10
 *
 * This controller is intentionally separated from the write-oriented
 * OrderController to highlight a dedicated "query" endpoint suitable for
 * dashboards and Angular data grids.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderQueryController {

	private final OrderQueryService orderQueryService;

	public OrderQueryController(OrderQueryService orderQueryService) {
		this.orderQueryService = orderQueryService;
	}

	/**
	 * Search orders with optional filters and pagination.
	 *
	 * NOTE: Path is "/api/orders/search" to avoid conflicting with the existing GET
	 * "/api/orders" mapping in OrderController.
	 *
	 * @param status   optional status filter (e.g. "NEW", "PROCESSING";
	 *                 case-insensitive)
	 * @param customer optional customer name substring (case-insensitive)
	 * @param page     page index (0-based), defaults to 0
	 * @param size     page size, defaults to 20
	 * @return a JSON page wrapper with content + paging metadata
	 */
	@GetMapping("/search")
	public ResponseEntity<OrdersPageResponse> searchOrders(
			@RequestParam(name = "status", required = false) String status,
			@RequestParam(name = "customer", required = false) String customer,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "20") int size) {

		OrdersPageResponse result = orderQueryService.findOrders(status, customer, page, size);
		return ResponseEntity.ok(result);
	}
}