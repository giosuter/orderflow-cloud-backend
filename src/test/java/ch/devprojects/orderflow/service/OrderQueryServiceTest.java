package ch.devprojects.orderflow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.repository.OrderRepository;

/**
 * Unit tests for {@link OrderQueryService}.
 *
 * Purpose: - Verify read-only query logic - Ensure correct delegation to
 * {@link OrderRepository} - Protect against accidental business logic leakage
 *
 * Style: - Pure unit test - No Spring context - Mockito-based isolation
 */
@ExtendWith(MockitoExtension.class)
class OrderQueryServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@InjectMocks
	private OrderQueryService orderQueryService;

	// ---------------------------------------------------------------------
	// findAll()
	// ---------------------------------------------------------------------

	@Test
	@DisplayName("findAll should return all orders from repository")
	void findAll_shouldReturnAllOrders() {
		// Arrange
		Order order1 = createOrder(1L, "ORD-001", OrderStatus.NEW);
		Order order2 = createOrder(2L, "ORD-002", OrderStatus.PROCESSING);

		when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

		// Act
		List<Order> result = orderQueryService.findAll();

		// Assert
		assertThat(result).hasSize(2).containsExactly(order1, order2);
	}

	// ---------------------------------------------------------------------
	// findById()
	// ---------------------------------------------------------------------

	@Test
	@DisplayName("findById should return order when found")
	void findById_shouldReturnOrder_whenFound() {
		// Arrange
		Order order = createOrder(10L, "ORD-010", OrderStatus.PAID);

		when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

		// Act
		Optional<Order> result = orderQueryService.findById(10L);

		// Assert
		assertThat(result).isPresent();
		assertThat(result.get().getCode()).isEqualTo("ORD-010");
	}

	@Test
	@DisplayName("findById should return empty when order not found")
	void findById_shouldReturnEmpty_whenNotFound() {
		// Arrange
		when(orderRepository.findById(99L)).thenReturn(Optional.empty());

		// Act
		Optional<Order> result = orderQueryService.findById(99L);

		// Assert
		assertThat(result).isEmpty();
	}

	// ---------------------------------------------------------------------
	// findByStatus()
	// ---------------------------------------------------------------------

	@Test
	@DisplayName("findByStatus should return orders with given status")
	void findByStatus_shouldReturnMatchingOrders() {
		// Arrange
		Order order1 = createOrder(1L, "ORD-100", OrderStatus.SHIPPED);
		Order order2 = createOrder(2L, "ORD-101", OrderStatus.SHIPPED);

		when(orderRepository.findByStatus(OrderStatus.SHIPPED)).thenReturn(List.of(order1, order2));

		// Act
		List<Order> result = orderQueryService.findByStatus(OrderStatus.SHIPPED);

		// Assert
		assertThat(result).hasSize(2).allMatch(order -> order.getStatus() == OrderStatus.SHIPPED);
	}

	// ---------------------------------------------------------------------
	// Helper
	// ---------------------------------------------------------------------

	/**
	 * Creates a minimal valid Order entity for testing. Adjust fields only if your
	 * domain model changes.
	 */
	private Order createOrder(Long id, String code, OrderStatus status) {
	    Order order = new Order();
	    order.setId(id);
	    order.setCode(code);
	    order.setStatus(status);
	    order.setTotal(BigDecimal.valueOf(99.90));
	    order.setCreatedAt(Instant.now()); // FIX: Instant instead of LocalDateTime
	    return order;
	}
}