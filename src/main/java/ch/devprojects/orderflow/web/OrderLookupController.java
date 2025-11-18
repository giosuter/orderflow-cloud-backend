package ch.devprojects.orderflow.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderService;

/**
 * OrderLookupController
 *
 * Responsibility:
 * - Expose a dedicated read-only endpoint for looking up an Order by its business code.
 *
 * Endpoint:
 * - GET /api/orders/by-code/{code}
 *
 * Behavior:
 * - Delegates to OrderService.findByCode(code).
 * - On success: returns 200 OK with the OrderDto JSON.
 * - On not found: OrderService throws EntityNotFoundException, which is translated
 *   to 404 by GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderLookupController {

    private final OrderService orderService;

    public OrderLookupController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Lookup an order by its code (case-insensitive).
     *
     * Example:
     *   GET /api/orders/by-code/ORD-123
     */
    @GetMapping("/by-code/{code}")
    public ResponseEntity<OrderDto> getByCode(@PathVariable("code") String code) {
        // Delegate to service. If not found, EntityNotFoundException will bubble up
        // and be handled by GlobalExceptionHandler.
        OrderDto dto = orderService.findByCode(code);
        return ResponseEntity.ok(dto);
    }
}