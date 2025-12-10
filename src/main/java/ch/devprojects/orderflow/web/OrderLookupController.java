package ch.devprojects.orderflow.web;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.devprojects.orderflow.dto.OrderResponseDto;
import ch.devprojects.orderflow.service.OrderLookupService;

/**
 * Lightweight read-only controller for looking up a single Order by its ID.
 *
 * IMPORTANT: - Path is intentionally different from OrderController#getOne to
 * avoid ambiguous mappings.
 *
 * Exposed endpoint: - GET /api/order-lookup/{id}
 *
 * This allows us to: - Keep the existing canonical endpoint GET
 * /api/orders/{id} in OrderController - Have a dedicated, clearly separated
 * lookup controller for experiments, refactorings, and unit tests.
 */
@RestController
@RequestMapping("/api/order-lookup") // <- DIFFERENT FROM /api/orders
public class OrderLookupController {

	private final OrderLookupService orderLookupService;

	/**
	 * Constructor-based dependency injection. Spring will provide an
	 * {@link OrderLookupService} bean.
	 */
	public OrderLookupController(OrderLookupService orderLookupService) {
		this.orderLookupService = orderLookupService;
	}

	/**
	 * Fetch a single order by its ID.
	 *
	 * @param id the technical identifier of the order
	 * @return 200 OK with the OrderResponseDto as JSON if found,<br>
	 *         404 NOT_FOUND if no order exists with the given ID.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable("id") Long id) {
		Optional<OrderResponseDto> maybeOrder = orderLookupService.findById(id);

		return maybeOrder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}
}