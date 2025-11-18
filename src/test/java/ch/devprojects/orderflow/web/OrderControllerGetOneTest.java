package ch.devprojects.orderflow.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

/**
 * Web slice test for {@link OrderController}.
 *
 * Focus: - Verify that GET /api/orders/{id}: * delegates to
 * OrderService.findById(...) * returns 200 OK with the expected JSON payload
 * 
 * This test: - increases coverage on the happy path of getOne(...) - does NOT
 * require a real database or Flyway (OrderService is mocked) - protects the
 * external REST contract (JSON structure, HTTP status)
 */
@WebMvcTest(OrderController.class)
class OrderControllerGetOneTest {

	/**
	 * Mock the service layer because in a web slice test we want to verify only
	 * controller behavior and JSON mapping, not persistence.
	 */
	@Autowired
	private OrderService orderService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("GET /api/orders/{id} should return 200 and the Order JSON when the order exists")
	void getOne_shouldReturnOrder_whenExists() throws Exception {
		// Arrange: stub the service to return a known DTO
		Long id = 42L;

		OrderDto dto = new OrderDto();
		dto.setId(id);
		dto.setCode("ORD-42");
		dto.setStatus(OrderStatus.NEW);
		dto.setTotal(BigDecimal.valueOf(123.45));

		when(orderService.findById(id)).thenReturn(dto);

		// Act + Assert: call the endpoint and verify the JSON response
		mockMvc.perform(get("/api/orders/{id}", id).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				// Controller returns JSON, we check content type and a few fields
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(id.intValue())).andExpect(jsonPath("$.code").value("ORD-42"))
				.andExpect(jsonPath("$.status").value("NEW")).andExpect(jsonPath("$.total").value(123.45));
	}
}