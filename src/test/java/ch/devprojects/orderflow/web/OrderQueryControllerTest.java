package ch.devprojects.orderflow.web;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
		OrderStatus statusParam = OrderStatus.NEW;
		int page = 0;
		int size = 20;

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

		when(orderQueryService.findOrders(eq(customer), eq(statusParam), eq(page), eq(size))).thenReturn(response);

		// Act + Assert
		mockMvc.perform(get("/api/orders/search").param("customer", customer).param("status", "NEW")
				.param("page", String.valueOf(page)).param("size", String.valueOf(size))).andExpect(status().isOk());

		verify(orderQueryService).findOrders(eq(customer), eq(statusParam), eq(page), eq(size));
	}

	@Test
	@DisplayName("GET /api/orders/search without parameters should still call service with defaults")
	void searchOrders_withoutParams_usesDefaults() throws Exception {
		OrdersPageResponse emptyResponse = new OrdersPageResponse();
		emptyResponse.setContent(List.of());
		emptyResponse.setPage(0);
		emptyResponse.setSize(20);
		emptyResponse.setTotalElements(0);
		emptyResponse.setTotalPages(0);

		when(orderQueryService.findOrders(Mockito.isNull(), Mockito.isNull(), eq(0), eq(20))).thenReturn(emptyResponse);

		mockMvc.perform(get("/api/orders/search")).andExpect(status().isOk());

		verify(orderQueryService).findOrders(Mockito.isNull(), Mockito.isNull(), eq(0), eq(20));
	}
}