package ch.devprojects.orderflow.web;

import java.math.BigDecimal;

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
 * Contract used by the Angular list page: GET
 * /api/orders/search?customer=&status=&page=&size=&sortBy=&sortDir=&totalMin=&totalMax=
 *
 * Notes: - "customer" matches code OR customerName. - "status" is an optional
 * OrderStatus enum. - Sorting is server-side: sortBy:
 * createdAt|code|customerName|total|status (default: createdAt) sortDir:
 * asc|desc (default: desc) - Totals filtering: totalMin: inclusive lower bound
 * totalMax: inclusive upper bound
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
			@RequestParam(name = "size", defaultValue = "20") int size,
			@RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
			@RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
			@RequestParam(name = "totalMin", required = false) BigDecimal totalMin,
			@RequestParam(name = "totalMax", required = false) BigDecimal totalMax) {

		OrdersPageResponse response = orderQueryService.findOrders(customer, status, page, size, sortBy, sortDir,
				totalMin, totalMax);

		return ResponseEntity.ok(response);
	}
}