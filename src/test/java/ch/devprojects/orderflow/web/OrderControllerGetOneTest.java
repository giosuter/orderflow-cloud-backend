package ch.devprojects.orderflow.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderQueryService;
import ch.devprojects.orderflow.service.OrderService;

/**
 * Web slice test for {@link OrderController} - lookup by id.
 *
 * Important: - In @WebMvcTest, service beans are not created. - Therefore
 * controller dependencies must be mocked: OrderService + OrderQueryService
 */
@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerGetOneTest {

	@MockitoBean
	private OrderService orderService;

	@MockitoBean
	private OrderQueryService orderQueryService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("GET /api/orders/{id} should return order")
	void getOne_shouldReturnOrder_whenExists() throws Exception {
		OrderDto dto = new OrderDto();
		dto.setId(1L);
		dto.setCode("ORD-1");
		dto.setStatus("NEW"); // DTO uses String
		dto.setTotal(BigDecimal.TEN);

		when(orderService.findById(1L)).thenReturn(dto);

		mockMvc.perform(get("/api/orders/{id}", 1L).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.code").value("ORD-1"))
				.andExpect(jsonPath("$.status").value("NEW")).andExpect(jsonPath("$.total").value(10));
	}
}