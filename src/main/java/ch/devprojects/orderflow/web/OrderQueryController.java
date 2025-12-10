package ch.devprojects.orderflow.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrdersPageResponse;
import ch.devprojects.orderflow.service.OrderQueryService;

/**
 * REST controller exposing read-only, filtered order queries.
 *
 * Endpoint: GET /api/orders/search
 *
 * Query parameters: - customer (optional): substring match on customer name
 * (case-insensitive) - status (optional): order status, one of NEW, PROCESSING,
 * PAID, SHIPPED, CANCELLED - page (optional): 0-based page index, default 0 -
 * size (optional): page size, default 20
 *
 * Example: GET /api/orders/search?customer=acme&status=NEW&page=0&size=10
 */
@RestController
@RequestMapping("/api/orders")
public class OrderQueryController {

	private final OrderQueryService orderQueryService;

	public OrderQueryController(OrderQueryService orderQueryService) {
		this.orderQueryService = orderQueryService;
	}

	@GetMapping("/search")
	public OrdersPageResponse searchOrders(@RequestParam(name = "customer", required = false) String customer,
			@RequestParam(name = "status", required = false) OrderStatus status,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "20") int size) {

		// NOTE: call signature matches the service: (customerQuery, status, page, size)
		return orderQueryService.findOrders(customer, status, page, size);
	}
}