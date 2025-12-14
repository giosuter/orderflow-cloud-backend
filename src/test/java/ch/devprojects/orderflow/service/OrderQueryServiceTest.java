package ch.devprojects.orderflow.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrdersPageResponse;

/**
 * Unit tests for {@link OrderQueryService}.
 *
 * These tests validate the in-memory stub filtering + paging behavior.
 */
class OrderQueryServiceTest {

	@Test
	@DisplayName("findOrders filters by status (NEW)")
	void findOrders_filtersByStatus() {
		// Arrange
		OrderQueryService service = new OrderQueryService();

		// Act
		OrdersPageResponse res = service.findOrders(null, OrderStatus.NEW, 0, 50);

		// Assert
		assertThat(res).isNotNull();
		assertThat(res.getContent()).isNotEmpty();
		assertThat(res.getContent()).allSatisfy(o -> assertThat(o.getStatus()).isEqualTo("NEW"));
	}

	@Test
	@DisplayName("findOrders matches term against code OR customerName")
	void findOrders_filtersByTerm_matchesCodeOrCustomer() {
		// Arrange
		OrderQueryService service = new OrderQueryService();

		// Act: term matches code
		OrdersPageResponse byCode = service.findOrders("ORD-2025-0001", null, 0, 50);

		// Assert
		assertThat(byCode.getContent()).hasSize(1);
		assertThat(byCode.getContent().get(0).getCode()).isEqualTo("ORD-2025-0001");

		// Act: term matches customerName
		OrdersPageResponse byCustomer = service.findOrders("Acme", null, 0, 50);

		// Assert: multiple Acme GmbH orders exist in the stub data
		assertThat(byCustomer.getContent()).isNotEmpty();
		assertThat(byCustomer.getContent()).allSatisfy(o -> assertThat(o.getCode()).isNotBlank());
	}

	@Test
	@DisplayName("findOrders paginates correctly")
	void findOrders_paginates() {
		OrderQueryService service = new OrderQueryService();

		OrdersPageResponse page0 = service.findOrders(null, null, 0, 5);
		OrdersPageResponse page1 = service.findOrders(null, null, 1, 5);

		assertThat(page0.getContent()).hasSize(5);
		assertThat(page1.getContent()).hasSize(5);
		assertThat(page0.getTotalElements()).isEqualTo(10);
		assertThat(page0.getTotalPages()).isEqualTo(2);
	}
}