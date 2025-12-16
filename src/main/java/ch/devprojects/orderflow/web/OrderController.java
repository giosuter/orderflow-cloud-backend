package ch.devprojects.orderflow.web;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderService;

/**
 * REST controller for core Order CRUD endpoints.
 *
 * Note: - This controller exposes a dedicated "lookup by code" endpoint: GET
 * /api/orders/code/{code}
 *
 * Why this exists: - Some clients/tests use a stable lookup URL based on a
 * human readable order code. - Without this mapping, Spring may treat
 * "/api/orders/code/..." as a static resource path
 * (ResourceHttpRequestHandler), causing a 500 in tests.
 *
 * Domain note: - The canonical free-text field is now "description" (replacing
 * old "comment"). That change is handled in DTO/entity/mapper/service layers;
 * this controller just forwards the DTO as-is.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = Objects.requireNonNull(orderService, "orderService must not be null");
	}

	/**
	 * Get all orders (simple list).
	 */
	@GetMapping
	public List<OrderDto> getAll() {
		return orderService.findAll();
	}

	/**
	 * Get one order by database id.
	 */
	@GetMapping("/{id}")
	public OrderDto getOne(@PathVariable Long id) {
		return orderService.findById(id);
	}

	/**
	 * Lookup an order by its business code.
	 *
	 * This is required by OrderControllerGetByCodeTest: GET /api/orders/code/{code}
	 */
	@GetMapping("/code/{code}")
	public OrderDto getByCode(@PathVariable String code) {
		return orderService.findByCode(code);
	}

	/**
	 * Create a new order.
	 *
	 * Returns: - 201 Created - Location header points to /api/orders/{id}
	 */
	@PostMapping
	public ResponseEntity<OrderDto> create(@RequestBody OrderDto dto) {
		OrderDto created = orderService.create(dto);

		// Defensive: created.getId() should exist after persistence.
		URI location = URI.create("/api/orders/" + created.getId());
		return ResponseEntity.created(location).body(created);
	}

	/**
	 * Update an existing order by id.
	 */
	@PutMapping("/{id}")
	public OrderDto update(@PathVariable Long id, @RequestBody OrderDto dto) {
		return orderService.update(id, dto);
	}

	/**
	 * Delete an order by id.
	 *
	 * Returns: - 204 No Content when deleted - 404 if not found (via service)
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		orderService.delete(id);
		return ResponseEntity.noContent().build();
	}
}