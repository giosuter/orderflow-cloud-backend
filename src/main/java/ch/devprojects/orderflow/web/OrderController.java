package ch.devprojects.orderflow.web;

import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * OrderController — the REST controller dedicated to Order CRUD
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService service;

	public OrderController(OrderService service) {
		this.service = service;
	}

	// -------------------------------
	// CREATE
	// -------------------------------
	@PostMapping
	public ResponseEntity<OrderDto> create(@Valid @RequestBody OrderDto dto) {
		OrderDto saved = service.create(dto);
		return ResponseEntity.ok(saved);
	}

	// -------------------------------
	// UPDATE
	// -------------------------------
	@PutMapping("/{id}")
	public ResponseEntity<OrderDto> update(@PathVariable Long id, @Valid @RequestBody OrderDto dto) {
		OrderDto updated = service.update(id, dto);
		return ResponseEntity.ok(updated);
	}

	// -------------------------------
	// GET ALL
	// -------------------------------
	@GetMapping
	public ResponseEntity<List<OrderDto>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}

	// -------------------------------
	// GET BY ID
	// -------------------------------
	@GetMapping("/{id}")
	public ResponseEntity<OrderDto> findById(@PathVariable Long id) {
		return ResponseEntity.ok(service.findById(id));
	}

	// -------------------------------
	// DELETE
	// -------------------------------
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}