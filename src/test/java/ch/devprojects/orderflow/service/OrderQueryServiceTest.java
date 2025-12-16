package ch.devprojects.orderflow.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.dto.OrdersPageResponse;
import ch.devprojects.orderflow.repository.OrderRepository;

/**
 * Unit tests for {@link OrderQueryServiceImpl}.
 *
 * Contract (Option A): - Entity uses OrderStatus (enum) - OrderDto uses
 * OrderStatus (enum)
 */
class OrderQueryServiceTest {

	@Test
	@DisplayName("findOrders should apply sorting and map DTOs (status as enum)")
	void findOrders_shouldApplySortingAndMapDtos() {
		// Arrange
		OrderRepository repo = Mockito.mock(OrderRepository.class);
		OrderQueryServiceImpl service = new OrderQueryServiceImpl(repo);

		Order o1 = new Order();
		o1.setId(10L);
		o1.setCode("ORD-10");
		o1.setCustomerName("Alice");
		o1.setTotal(BigDecimal.valueOf(20));
		o1.setStatus(OrderStatus.NEW);
		o1.setCreatedAt(Instant.parse("2025-01-01T10:00:00Z"));
		o1.setUpdatedAt(Instant.parse("2025-01-01T10:05:00Z"));

		Order o2 = new Order();
		o2.setId(11L);
		o2.setCode("ORD-11");
		o2.setCustomerName("Bob");
		o2.setTotal(BigDecimal.valueOf(30));
		o2.setStatus(OrderStatus.PAID);
		o2.setCreatedAt(Instant.parse("2025-01-02T10:00:00Z"));
		o2.setUpdatedAt(Instant.parse("2025-01-02T10:05:00Z"));

		Page<Order> page = new PageImpl<>(List.of(o1, o2), PageRequest.of(0, 2), 2);

		// Capture the pageable passed to the repository call
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

		// IMPORTANT:
		// Explicitly type the matcher to Specification<Order> to avoid ambiguous
		// overload resolution.
		Mockito.when(repo.findAll(Mockito.<Specification<Order>>any(), pageableCaptor.capture())).thenReturn(page);

		// Act
		OrdersPageResponse resp = service.findOrders(null, null, 0, 2, "createdAt", "desc", null, null);

		// Assert: pageable sorting applied
		Pageable used = pageableCaptor.getValue();
		assertThat(used.getPageNumber()).isEqualTo(0);
		assertThat(used.getPageSize()).isEqualTo(2);

		Sort.Order sort = used.getSort().getOrderFor("createdAt");
		assertThat(sort).isNotNull();
		assertThat(sort.getDirection()).isEqualTo(Sort.Direction.DESC);

		// Assert: DTO mapping uses enum status
		assertThat(resp).isNotNull();
		assertThat(resp.getContent()).hasSize(2);

		OrderDto d1 = resp.getContent().get(0);
		assertThat(d1.getId()).isEqualTo(10L);
		assertThat(d1.getCode()).isEqualTo("ORD-10");
		assertThat(d1.getStatus()).isEqualTo("NEW");

		OrderDto d2 = resp.getContent().get(1);
		assertThat(d2.getId()).isEqualTo(11L);
		assertThat(d2.getCode()).isEqualTo("ORD-11");
		assertThat(d2.getStatus()).isEqualTo("PAID");
	}
}