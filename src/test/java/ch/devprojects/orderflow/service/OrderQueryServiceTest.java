package ch.devprojects.orderflow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrdersPageResponse;
import ch.devprojects.orderflow.repository.OrderRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.domain.Specification;

/**
 * Unit tests for {@link OrderQueryService}.
 *
 * Focus: - Pageable + Sort creation - Specification composition (basic) -
 * Mapping into OrdersPageResponse DTO
 */
@ExtendWith(MockitoExtension.class)
class OrderQueryServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@InjectMocks
	private OrderQueryService orderQueryService;

	@Test
	@DisplayName("findOrders should apply sortBy=code sortDir=asc into Pageable")
	void findOrders_shouldApplySorting() {
		// Arrange
		Order order = createOrder(1L, "ORD-001", OrderStatus.NEW);
		Page<Order> page = new PageImpl<>(List.of(order));

		when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

		// Act
		OrdersPageResponse response = orderQueryService.findOrders("alice", OrderStatus.NEW, 0, 20, "code", "asc");

		// Assert mapping basics
		assertThat(response.getContent()).hasSize(1);
		assertThat(response.getContent().get(0).getCode()).isEqualTo("ORD-001");
		assertThat(response.getContent().get(0).getStatus()).isEqualTo("NEW");

		// Capture Pageable and assert sorting
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(orderRepository).findAll(any(Specification.class), pageableCaptor.capture());

		Pageable used = pageableCaptor.getValue();
		assertThat(used.getPageNumber()).isEqualTo(0);
		assertThat(used.getPageSize()).isEqualTo(20);
		assertThat(used.getSort().getOrderFor("code")).isNotNull();
		assertThat(used.getSort().getOrderFor("code").isAscending()).isTrue();
	}

	@Test
	@DisplayName("findOrders should fallback to createdAt desc when sortBy is invalid")
	void findOrders_shouldFallbackWhenSortByInvalid() {
		// Arrange
		Order order = createOrder(1L, "ORD-001", OrderStatus.NEW);
		Page<Order> page = new PageImpl<>(List.of(order));

		when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

		// Act
		orderQueryService.findOrders(null, null, 0, 20, "DROP_TABLES", "asc");

		// Assert: sort should be by createdAt (desc unless sortDir=asc and allowed, but
		// fallback keeps field)
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(orderRepository).findAll(any(Specification.class), pageableCaptor.capture());

		Pageable used = pageableCaptor.getValue();
		assertThat(used.getSort().getOrderFor("createdAt")).isNotNull();
	}

	private Order createOrder(Long id, String code, OrderStatus status) {
		Order order = new Order();
		order.setId(id);
		order.setCode(code);
		order.setStatus(status);
		order.setCustomerName("Alice");
		order.setTotal(BigDecimal.valueOf(99.90));
		order.setCreatedAt(Instant.now());
		return order;
	}
}