package ch.devprojects.orderflow.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderService;

/**
 * OrderSearchController — dedicated controller for search use cases.
 *
 * Responsibility:
 *  - Expose search/filter endpoints for orders.
 *
 * Design:
 *  - Shares the same base path as OrderController (/api/orders),
 *    but only provides /search.
 *  - This keeps the external contract (URL) stable while allowing
 *    a clear separation of concerns inside the codebase.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderSearchController {

    private final OrderService orderService;

    public OrderSearchController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Search orders by optional code and status.
     *
     * Example:
     *  - GET /api/orders/search?code=ABC&status=NEW
     *  - GET /api/orders/search?code=ABC
     *  - GET /api/orders/search?status=PAID
     *
     * Both parameters are optional; when omitted, all orders are returned
     * (delegated to the service/specification layer).
     */
    @GetMapping("/search")
    public ResponseEntity<List<OrderDto>> search(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "status", required = false) OrderStatus status) {

        List<OrderDto> results = orderService.search(code, status);
        return ResponseEntity.ok(results);
    }
}