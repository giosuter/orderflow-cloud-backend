package ch.devprojects.orderflow.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderQueryService;
import ch.devprojects.orderflow.service.OrderService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * WebMvc test for OrderController GET /api/orders.
 *
 * IMPORTANT: - Controller requires OrderService AND OrderQueryService - Both
 * must be mocked for @WebMvcTest slice
 */
@WebMvcTest(controllers = OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerGetAllTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderService orderService;

	@MockBean
	private OrderQueryService orderQueryService;

	@Test
	@DisplayName("getAll should return list of orders")
	void getAll_shouldReturnListOfOrders() throws Exception {
		OrderDto dto = new OrderDto();
		dto.setId(1L);
		dto.setCode("ORD-1");
		dto.setCustomerName("Alice");
		dto.setTotal(BigDecimal.TEN);
		dto.setStatus("NEW");
		dto.setCreatedAt(Instant.parse("2025-01-01T00:00:00Z"));
		dto.setUpdatedAt(Instant.parse("2025-01-01T00:00:00Z"));

		when(orderService.findAll()).thenReturn(List.of(dto));

		mockMvc.perform(get("/api/orders").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].id").value(1)).andExpect(jsonPath("$[0].code").value("ORD-1"))
				.andExpect(jsonPath("$[0].status").value("NEW"));
	}
}