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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderService;

/**
 * Web slice test for {@link OrderController}.
 *
 * <p>
 * Focus:
 * </p>
 * <ul>
 * <li>Verify that GET /api/orders/{id}:</li>
 * <li>delegates to {@link OrderService#findById(Long)}</li>
 * <li>returns 200 OK with the expected JSON payload</li>
 * </ul>
 *
 * <p>
 * This test:
 * </p>
 * <ul>
 * <li>increases coverage on the happy path of {@code getOne(...)};</li>
 * <li>does not require a real database or Flyway (the {@link OrderService} is
 * mocked);</li>
 * <li>protects the external REST contract (JSON structure, HTTP status).</li>
 * </ul>
 */
@WebMvcTest(OrderController.class)
class OrderControllerGetOneTest {

	/**
	 * Mockito-based mock of {@link OrderService} for this web slice test.
	 *
	 * <p>
	 * In a {@link WebMvcTest} context, service beans are not created by default.
	 * {@link MockitoBean} ensures that the controller under test can be constructed
	 * with a valid {@link OrderService} dependency while still allowing us to
	 * control its behaviour via Mockito.
	 * </p>
	 */
	@MockitoBean
	private OrderService orderService;

	/**
	 * {@link MockMvc} simulates HTTP requests to the controller without starting a
	 * real servlet container. This keeps the tests fast while still exercising the
	 * full Spring MVC stack (routing, JSON mapping, etc.).
	 */
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
		mockMvc.perform(get("/api/orders/{id}", id).accept(MediaType.APPLICATION_JSON))
				// Controller returns JSON, we check content type and a few fields
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(id.intValue())).andExpect(jsonPath("$.code").value("ORD-42"))
				.andExpect(jsonPath("$.status").value("NEW")).andExpect(jsonPath("$.total").value(123.45));
	}
}