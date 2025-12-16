package ch.devprojects.orderflow.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderQueryService;

/**
 * MVC test for OrderQueryController.
 *
 * IMPORTANT: - Endpoint is /api/orders/query (NOT /api/orders/search)
 */
@WebMvcTest(controllers = OrderQueryController.class)
class OrderQueryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderQueryService orderQueryService;

	@Test
	@DisplayName("GET /api/orders/query should return 200")
	void getQuery_shouldReturnOk() throws Exception {
		OrderDto dto = new OrderDto();
		dto.setId(1L);
		dto.setCode("ORD-001");
		dto.setStatus("NEW");

		Page<OrderDto> page = new PageImpl<>(List.of(dto));

		when(orderQueryService.search(any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

		mockMvc.perform(get("/api/orders/query")).andExpect(status().isOk());
	}
}