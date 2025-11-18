package ch.devprojects.orderflow.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderService;
import jakarta.validation.Valid;

/**
 * REST controller for Order CRUD.
 *
 * Responsibilities:
 * - Map HTTP requests to service calls.
 * - Apply request validation (@Valid on DTOs).
 * - Return appropriate HTTP status codes (201, 200, 204, 400, 404).
 *
 * Note:
 * - Error handling is delegated to GlobalExceptionHandler.
 */
@RestController
@RequestMapping(path = "/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    /** Create a new order (201 Created). */
    @PostMapping
    public ResponseEntity<OrderDto> create(@Valid @RequestBody OrderDto dto, UriComponentsBuilder uriBuilder) {
        OrderDto created = orderService.create(dto);
        URI location = uriBuilder.path("/api/orders/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created); // 201
    }

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

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

    /**
     * Search orders by optional filters: code (substring) and status.
     *
     * Example:
     *   GET /api/orders/search?code=ORD&status=NEW
     *
     * - If no parameters are provided, returns all orders.
     * - If only code is provided, filters by code.
     * - If only status is provided, filters by status.
     * - If both are provided, applies both filters.
     */
    @GetMapping("/search")
    public ResponseEntity<List<OrderDto>> search(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "status", required = false) OrderStatus status) {

        List<OrderDto> result = orderService.search(code, status);
        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    /** Update an existing order (200 OK). */
    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> update(@PathVariable Long id, @Valid @RequestBody OrderDto dto) {
        return ResponseEntity.ok(orderService.update(id, dto));
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    /** Delete an order (204 No Content). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}