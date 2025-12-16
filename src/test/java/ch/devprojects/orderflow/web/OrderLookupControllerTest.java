package ch.devprojects.orderflow.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderResponseDto;
import ch.devprojects.orderflow.service.OrderLookupService;

/**
 * Web slice test for OrderLookupController.
 *
 * Endpoint under test: - GET /api/order-lookup/{id}
 */
@WebMvcTest(OrderLookupController.class)
class OrderLookupControllerTest {

	@MockitoBean
	private OrderLookupService orderLookupService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("GET /api/order-lookup/{id} should return 200 and the order JSON when found")
	void getOrderById_shouldReturnOrder_whenExists() throws Exception {
		OrderResponseDto dto = new OrderResponseDto();
		dto.setId(1L);
		dto.setCode("ORD-LOOK");
		dto.setCustomerName("Alice");
		dto.setTotal(BigDecimal.TEN);
		dto.setStatus("NEW");
		dto.setCreatedAt(Instant.parse("2025-01-01T10:00:00Z"));
		dto.setUpdatedAt(Instant.parse("2025-01-01T10:05:00Z"));

		when(orderLookupService.findById(1L)).thenReturn(Optional.of(dto));

		mockMvc.perform(get("/api/order-lookup/{id}", 1L)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.code").value("ORD-LOOK"))
				.andExpect(jsonPath("$.status").value("NEW"));
	}
}