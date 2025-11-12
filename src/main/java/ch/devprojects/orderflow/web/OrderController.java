package ch.devprojects.orderflow.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * REST controller for Order CRUD. - POST returns 201 Created + Location header
 * (RFC 9110). - Other endpoints return 200 OK.
 */
@RestController
@RequestMapping(path = "/api/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	/** Create a new order (201 Created). */
	@PostMapping
	public ResponseEntity<OrderDto> create(@Valid @RequestBody OrderDto dto, UriComponentsBuilder uriBuilder) {
		OrderDto created = orderService.create(dto);
		URI location = uriBuilder.path("/api/orders/{id}").buildAndExpand(created.getId()).toUri();
		return ResponseEntity.created(location).body(created); // <-- 201
	}

	/** Get one order by id (200 OK). */
	@GetMapping("/{id}")
	public ResponseEntity<OrderDto> getOne(@PathVariable Long id) {
		return ResponseEntity.ok(orderService.findById(id));
	}

	/** List all orders (200 OK). */
	@GetMapping
	public ResponseEntity<List<OrderDto>> getAll() {
		return ResponseEntity.ok(orderService.findAll());
	}

	/** Update an existing order (200 OK). */
	@PutMapping("/{id}")
	public ResponseEntity<OrderDto> update(@PathVariable Long id, @Valid @RequestBody OrderDto dto) {
		return ResponseEntity.ok(orderService.update(id, dto));
	}

	/** Delete an order (204 No Content). */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		orderService.delete(id);
		return ResponseEntity.noContent().build();
	}
}