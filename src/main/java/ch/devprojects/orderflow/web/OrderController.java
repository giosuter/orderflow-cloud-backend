package ch.devprojects.orderflow.web;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderService;

/**
 * REST controller for core Order CRUD endpoints.
 *
 * Notes:
 * - The frontend calls GET /api/orders/search?page=0&size=5 to load a paged list.
 * - If /search is missing, Spring may route "search" into /{id} and fail with
 *   MethodArgumentTypeMismatchException (String "search" -> Long).
 * - To avoid that, we provide an explicit /search mapping AND we constrain {id}
 *   to digits only.
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
     * Paged list endpoint used by the Angular frontend:
     * GET /api/orders/search?page=0&size=5
     *
     * Implementation note:
     * - We keep it backend-safe and minimal for now by paging in memory using findAll().
     * - Later we can replace this with a real repository/page query without changing the URL.
     */
    @GetMapping("/search")
    public Page<OrderDto> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (page < 0) {
            page = 0;
        }
        if (size < 1) {
            size = 1;
        }

        List<OrderDto> all = orderService.findAll();

        int fromIndex = Math.min(page * size, all.size());
        int toIndex = Math.min(fromIndex + size, all.size());

        List<OrderDto> content = all.subList(fromIndex, toIndex);

        return new PageImpl<>(content, PageRequest.of(page, size), all.size());
    }

    /**
     * Lookup an order by its business code.
     */
    @GetMapping("/code/{code}")
    public OrderDto getByCode(@PathVariable String code) {
        return orderService.findByCode(code);
    }

    /**
     * Get one order by database id.
     *
     * Important:
     * - Constrain {id} to digits only, so "/search" can never be treated as an id.
     */
    @GetMapping("/{id:\\d+}")
    public OrderDto getOne(@PathVariable Long id) {
        return orderService.findById(id);
    }

    /**
     * Create a new order.
     *
     * Returns:
     * - 201 Created
     * - Location header points to /api/orders/{id}
     */
    @PostMapping
    public ResponseEntity<OrderDto> create(@RequestBody OrderDto dto) {
        OrderDto created = orderService.create(dto);
        URI location = URI.create("/api/orders/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Update an existing order by id.
     */
    @PutMapping("/{id:\\d+}")
    public OrderDto update(@PathVariable Long id, @RequestBody OrderDto dto) {
        return orderService.update(id, dto);
    }

    /**
     * Delete an order by id.
     *
     * Returns:
     * - 204 No Content when deleted
     * - 404 if not found (via service)
     */
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}