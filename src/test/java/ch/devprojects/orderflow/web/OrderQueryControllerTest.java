package ch.devprojects.orderflow.web;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderResponseDto;
import ch.devprojects.orderflow.dto.OrdersPageResponse;
import ch.devprojects.orderflow.service.OrderQueryService;

/**
 * Web MVC slice test for {@link OrderQueryController}.
 *
 * Verifies: - /api/orders/search endpoint is available - request parameters are
 * correctly mapped and forwarded to OrderQueryService
 *
 * Backend contract: GET /api/orders/search?customer=&status=&page=&size= ->
 * OrderQueryService.findOrders(customerTerm, status, page, size)
 */
@WebMvcTest(controllers = OrderQueryController.class)
class OrderQueryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderQueryService orderQueryService;

	@Test
	@DisplayName("GET /api/orders/search forwards parameters to OrderQueryService.findOrders")
	void searchOrders_forwardsParametersToService() throws Exception {
		// Arrange
		String customer = "Acme";
		OrderStatus status = OrderStatus.NEW;
		int page = 0;
		int size = 5;

		OrdersPageResponse response = new OrdersPageResponse();
		response.setPage(page);
		response.setSize(size);
		response.setTotalElements(1);
		response.setTotalPages(1);

		OrderResponseDto dto = new OrderResponseDto();
		dto.setId(1L);
		dto.setCode("ORD-2025-0001");
		dto.setStatus("NEW");
		dto.setCustomerName("Acme GmbH");
		dto.setAssignedTo("Giovanni Suter");
		dto.setTotal(new BigDecimal("120.50"));
		dto.setCreatedAt(LocalDateTime.now().minusDays(1));

		response.setContent(List.of(dto));

		when(orderQueryService.findOrders(eq(customer), eq(status), eq(page), eq(size))).thenReturn(response);

		// Act + Assert
		mockMvc.perform(get("/api/orders/search").param("customer", customer).param("status", "NEW") // controller binds
																										// this to
																										// OrderStatus.NEW
				.param("page", String.valueOf(page)).param("size", String.valueOf(size))).andExpect(status().isOk());

		// Verify correct delegation (customer first, status second)
		verify(orderQueryService).findOrders(eq(customer), eq(status), eq(page), eq(size));
	}

	@Test
	@DisplayName("GET /api/orders/search without parameters calls service with defaults (null filters, page=0, size=5)")
	void searchOrders_withoutParams_usesDefaults() throws Exception {
		// Arrange: controller defaults must match your controller method defaults
		int defaultPage = 0;
		int defaultSize = 5;

		OrdersPageResponse emptyResponse = new OrdersPageResponse();
		emptyResponse.setContent(List.of());
		emptyResponse.setPage(defaultPage);
		emptyResponse.setSize(defaultSize);
		emptyResponse.setTotalElements(0);
		emptyResponse.setTotalPages(0);

		when(orderQueryService.findOrders(isNull(), isNull(), eq(defaultPage), eq(defaultSize)))
				.thenReturn(emptyResponse);

		// Act + Assert
		mockMvc.perform(get("/api/orders/search")).andExpect(status().isOk());

		// Verify delegation with null filters + defaults
		verify(orderQueryService).findOrders(isNull(), isNull(), eq(defaultPage), eq(defaultSize));
	}
}