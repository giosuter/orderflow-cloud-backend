package ch.devprojects.orderflow.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderService;
import jakarta.validation.Valid;

/**
 * REST controller for basic CRUD operations on Orders.
 *
 * Responsibilities:
 *  - Expose resource-oriented endpoints under /api/orders
 *  - Delegate business logic to OrderService
 *  - Keep HTTP details (status codes, locations) close to the edge
 *
 * NOTE:
 *  - Search is handled in a dedicated OrderSearchController to keep
 *    responsibilities clear and tests focused.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------

    @PostMapping
    public ResponseEntity<OrderDto> create(@Valid @RequestBody OrderDto dto) {
        OrderDto created = orderService.create(dto);
        // Build a simple Location header: /api/orders/{id}
        URI location = URI.create("/api/orders/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    // ------------------------------------------------------------
    // READ (one)
    // ------------------------------------------------------------

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOne(@PathVariable Long id) {
        OrderDto found = orderService.findById(id);
        return ResponseEntity.ok(found);
    }

    // ------------------------------------------------------------
    // READ (all)
    // ------------------------------------------------------------

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAll() {
        List<OrderDto> all = orderService.findAll();
        return ResponseEntity.ok(all);
    }

    // ------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> update(@PathVariable Long id,
                                           @Valid @RequestBody OrderDto dto) {
        OrderDto updated = orderService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // ------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // IMPORTANT:
    //  - There is intentionally NO /search endpoint here anymore.
    //  - Search is moved into OrderSearchController to avoid ambiguous
    //    mappings and to keep concerns separated.
    
    @GetMapping("/code/{code}")
    public ResponseEntity<OrderDto> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(orderService.findByCode(code));
    }
}