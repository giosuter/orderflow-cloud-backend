package ch.devprojects.orderflow.web;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderQueryService;

/**
 * Advanced query endpoints.
 *
 * IMPORTANT: - OrderController already owns GET /api/orders/search - Therefore
 * this controller MUST NOT map to /api/orders/search - We use /api/orders/query
 * to avoid ambiguous mapping at startup.
 */
@RestController
@RequestMapping("/api/orders/query")
public class OrderQueryController {

	private final OrderQueryService orderQueryService;

	public OrderQueryController(OrderQueryService orderQueryService) {
		this.orderQueryService = orderQueryService;
	}

	/**
	 * Advanced search (paged + many optional filters).
	 *
	 * GET
	 * /api/orders/query?customer=...&status=...&page=0&size=20&codeFrom=...&codeTo=...&totalMin=...&totalMax=...
	 */
	@GetMapping
	public Page<OrderDto> search(@RequestParam(required = false) String customer,
			@RequestParam(required = false) OrderStatus status, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String codeFrom,
			@RequestParam(required = false) String codeTo, @RequestParam(required = false) BigDecimal totalMin,
			@RequestParam(required = false) BigDecimal totalMax) {

		Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
		return orderQueryService.search(customer, status, codeFrom, codeTo, totalMin, totalMax, pageable);
	}
}