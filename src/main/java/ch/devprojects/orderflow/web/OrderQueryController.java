package ch.devprojects.orderflow.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrdersPageResponse;
import ch.devprojects.orderflow.service.OrderQueryService;

/**
 * Read-only query endpoints for Orders.
 *
 * Current contract used by the Angular list page: GET
 * /api/orders/search?customer=&status=&page=&size=
 *
 * Notes: - "customer" is currently a free-text term that matches code OR
 * customerName. - "status" is an optional OrderStatus enum.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderQueryController {

	private final OrderQueryService orderQueryService;

	public OrderQueryController(OrderQueryService orderQueryService) {
		this.orderQueryService = orderQueryService;
	}

	@GetMapping("/search")
	public ResponseEntity<OrdersPageResponse> search(@RequestParam(name = "customer", required = false) String customer,
			@RequestParam(name = "status", required = false) OrderStatus status,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "20") int size) {

		OrdersPageResponse response = orderQueryService.findOrders(customer, status, page, size);
		return ResponseEntity.ok(response);
	}
}