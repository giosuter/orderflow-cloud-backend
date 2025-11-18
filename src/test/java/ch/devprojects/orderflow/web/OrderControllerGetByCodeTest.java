package ch.devprojects.orderflow.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderService;

@WebMvcTest(OrderController.class)
class OrderControllerGetByCodeTest {

	@Autowired
	private OrderService orderService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("GET /api/orders/code/{code} returns OrderDto")
	void getByCode_shouldReturnOrder_whenExists() throws Exception {
		OrderDto dto = new OrderDto();
		dto.setId(7L);
		dto.setCode("XYZ777");
		dto.setStatus(OrderStatus.NEW);
		dto.setTotal(BigDecimal.TEN);

		when(orderService.findByCode("XYZ777")).thenReturn(dto);

		mockMvc.perform(get("/api/orders/code/{code}", "XYZ777").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(7))
				.andExpect(jsonPath("$.code").value("XYZ777")).andExpect(jsonPath("$.status").value("NEW"))
				.andExpect(jsonPath("$.total").value(10));
	}
}