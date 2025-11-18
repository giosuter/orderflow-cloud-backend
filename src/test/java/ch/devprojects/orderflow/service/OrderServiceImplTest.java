package ch.devprojects.orderflow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.mapper.OrderMapper;
import ch.devprojects.orderflow.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;

/**
 * Unit tests for {@link OrderServiceImpl}.
 *
 * Focus:
 *  - Exercise business logic and mapping in the service layer.
 *  - Uses a mocked OrderRepository (no database, no Flyway).
 *  - Uses the real OrderMapper for entity <-> DTO mapping.
 *
 * Covered scenarios:
 *  - create: happy path + validation failures
 *  - findById: existing + missing
 *  - findAll: simple passthrough
 *  - update: existing + missing
 *  - delete: existing + missing
 *  - search: different combinations of code/status filters
 */
@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    // We use the real mapper; it's simple and deterministic.
    private OrderMapper orderMapper;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        this.orderMapper = new OrderMapper();
        this.orderService = new OrderServiceImpl(orderRepository);
    }

    // -------------------------------------------------------------------------
    // Helper: create a sample domain Order
    // -------------------------------------------------------------------------
    private Order createSampleOrder(Long id) {
        Order order = new Order();
        order.setId(id);
        order.setCode("ORD-" + id);
        order.setStatus(OrderStatus.NEW);
        order.setTotal(BigDecimal.valueOf(100.50));
        order.setCreatedAt(Instant.parse("2025-01-01T10:00:00Z"));
        order.setUpdatedAt(Instant.parse("2025-01-01T11:00:00Z"));
        return order;
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("create() should set defaults and timestamps and save the order")
    void create_shouldSetDefaultsAndSave() {
        // Arrange: DTO without status or timestamps
        OrderDto dto = new OrderDto();
        dto.setCode("ORD-1");
        dto.setTotal(BigDecimal.valueOf(50.00));
        // dto.setStatus(null) -> default must be NEW

        // When saving, we simulate that the DB assigns an ID and echoes the entity
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        // Act
        OrderDto result = orderService.create(dto);

        // Assert
        assertNotNull(result, "Result DTO must not be null");
        assertEquals(1L, result.getId(), "ID should be set by the repository");
        assertEquals("ORD-1", result.getCode(), "Code should be propagated");
        assertEquals(OrderStatus.NEW, result.getStatus(), "Status should default to NEW");

        // Capture the saved entity to check timestamps and defaults at the entity level
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order savedEntity = orderCaptor.getValue();

        assertEquals("ORD-1", savedEntity.getCode());
        assertEquals(OrderStatus.NEW, savedEntity.getStatus(), "Status must never be null");
        assertNotNull(savedEntity.getCreatedAt(), "createdAt must be initialized");
        assertNotNull(savedEntity.getUpdatedAt(), "updatedAt must be initialized");
    }

    @Test
    @DisplayName("create() should throw IllegalArgumentException when code is blank")
    void create_shouldThrowWhenCodeBlank() {
        OrderDto dto = new OrderDto();
        dto.setCode("   "); // blank
        dto.setTotal(BigDecimal.TEN);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.create(dto),
                "Blank code must cause IllegalArgumentException"
        );
    }

    @Test
    @DisplayName("create() should throw IllegalArgumentException when total is null")
    void create_shouldThrowWhenTotalIsNull() {
        OrderDto dto = new OrderDto();
        dto.setCode("ORD-2");
        dto.setTotal(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.create(dto),
                "Null total must cause IllegalArgumentException"
        );
    }

    // -------------------------------------------------------------------------
    // FIND BY ID
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findById() should return the mapped DTO when the order exists")
    void findById_shouldReturnDtoWhenExists() {
        // Arrange
        Order order = createSampleOrder(10L);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        // Act
        OrderDto result = orderService.findById(10L);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("ORD-10", result.getCode());
        assertEquals(OrderStatus.NEW, result.getStatus());
    }

    @Test
    @DisplayName("findById() should throw EntityNotFoundException when the order does not exist")
    void findById_shouldThrowWhenMissing() {
        // Arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                EntityNotFoundException.class,
                () -> orderService.findById(999L),
                "Missing order must trigger EntityNotFoundException"
        );
    }

    // -------------------------------------------------------------------------
    // FIND ALL
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findAll() should map all entities to DTOs")
    void findAll_shouldReturnMappedDtos() {
        // Arrange
        Order o1 = createSampleOrder(1L);
        Order o2 = createSampleOrder(2L);
        when(orderRepository.findAll()).thenReturn(List.of(o1, o2));

        // Act
        List<OrderDto> result = orderService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size(), "Should return two DTOs");

        assertEquals(1L, result.get(0).getId());
        assertEquals("ORD-1", result.get(0).getCode());

        assertEquals(2L, result.get(1).getId());
        assertEquals("ORD-2", result.get(1).getCode());
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("update() should apply non-null fields and save the entity")
    void update_shouldApplyChangesAndSave() {
        // Arrange: existing entity in DB
        Order existing = createSampleOrder(5L);
        existing.setStatus(OrderStatus.NEW);

        when(orderRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // DTO with some updated fields (partial update)
        OrderDto dto = new OrderDto();
        dto.setCode("ORD-5-UPDATED");
        dto.setStatus(OrderStatus.PAID);          // change status
        dto.setTotal(BigDecimal.valueOf(999.99)); // change total

        // Act
        OrderDto result = orderService.update(5L, dto);

        // Assert: returned DTO
        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("ORD-5-UPDATED", result.getCode());
        assertEquals(OrderStatus.PAID, result.getStatus());
        assertEquals(BigDecimal.valueOf(999.99), result.getTotal());

        // Verify that repository.save was invoked with an updated entity
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order savedEntity = orderCaptor.getValue();

        assertEquals("ORD-5-UPDATED", savedEntity.getCode());
        assertEquals(OrderStatus.PAID, savedEntity.getStatus());
        assertEquals(BigDecimal.valueOf(999.99), savedEntity.getTotal());
        assertNotNull(savedEntity.getUpdatedAt(), "updatedAt must be maintained");
    }

    @Test
    @DisplayName("update() should throw EntityNotFoundException when the order does not exist")
    void update_shouldThrowWhenMissing() {
        // Arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        OrderDto dto = new OrderDto();
        dto.setCode("ORD-X");
        dto.setTotal(BigDecimal.ONE);

        // Act + Assert
        assertThrows(
                EntityNotFoundException.class,
                () -> orderService.update(123L, dto),
                "Updating a missing order must trigger EntityNotFoundException"
        );
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("delete() should delete the order when it exists")
    void delete_shouldRemoveWhenExists() {
        // Arrange
        when(orderRepository.existsById(7L)).thenReturn(true);

        // Act
        orderService.delete(7L);

        // Assert
        verify(orderRepository).deleteById(7L);
    }

    @Test
    @DisplayName("delete() should throw EntityNotFoundException when the order does not exist")
    void delete_shouldThrowWhenMissing() {
        // Arrange
        when(orderRepository.existsById(anyLong())).thenReturn(false);

        // Act + Assert
        assertThrows(
                EntityNotFoundException.class,
                () -> orderService.delete(999L),
                "Deleting a missing order must trigger EntityNotFoundException"
        );
    }

    // -------------------------------------------------------------------------
    // SEARCH
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("search() with no filters should call repository.findAll(spec)")
    void search_withoutFilters_shouldDelegateToRepository() {
        // Arrange: repository returns a single order for the given spec (we don't care how the spec looks)
        Order order = createSampleOrder(1L);
        when(orderRepository.findAll(any(Specification.class))).thenReturn(List.of(order));

        // Act
        List<OrderDto> result = orderService.search(null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ORD-1", result.get(0).getCode());

        // Verify that we did indeed call the Specification-based findAll
        verify(orderRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("search() with code and status filters should still delegate to repository.findAll(spec)")
    void search_withCodeAndStatus_shouldDelegateToRepository() {
        // Arrange: again we only care that the service delegates correctly
        Order order = createSampleOrder(2L);
        when(orderRepository.findAll(any(Specification.class))).thenReturn(List.of(order));

        // Act
        List<OrderDto> result = orderService.search("ORD-2", OrderStatus.NEW);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ORD-2", result.get(0).getCode());
        assertEquals(OrderStatus.NEW, result.get(0).getStatus());

        // Even though we don't inspect the Specification, this line ensures
        // that the Specification-based path in OrderServiceImpl is executed.
        verify(orderRepository).findAll(any(Specification.class));
    }
}