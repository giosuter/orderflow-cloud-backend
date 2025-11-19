package ch.devprojects.orderflow.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

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
 * Web slice test for {@link OrderController} - "list all" endpoint.
 *
 * <p>
 * Focus:
 * </p>
 * <ul>
 * <li>GET /api/orders</li>
 * <li>Ensure we get HTTP 200 and a JSON array with the expected elements.</li>
 * </ul>
 *
 * <p>
 * This is a pure web-layer test: we do not hit the real database or the real
 * {@code OrderService}. Instead, we mock the service and verify that the
 * controller correctly maps its result to the HTTP response.
 * </p>
 */
@WebMvcTest(OrderController.class)
class OrderControllerGetAllTest {

	/**
	 * Mockito-based mock of {@link OrderService} for the web layer test.
	 *
	 * <p>
	 * In a {@link WebMvcTest} slice, Spring Boot does not create real
	 * {@code @Service} beans. By providing a {@code @MockitoBean} here, we ensure
	 * that {@link OrderController} can be instantiated with a valid
	 * {@link OrderService} dependency in the test ApplicationContext.
	 * </p>
	 *
	 * <p>
	 * Individual test methods can define the behaviour of this mock using Mockito's
	 * {@code when(...)} API.
	 * </p>
	 */
	@MockitoBean
	private OrderService orderService;

	/**
	 * MockMvc simulates HTTP requests to the controller without starting a full
	 * servlet container. This keeps the test fast while still exercising the Spring
	 * MVC stack (routing, JSON mapping, etc.).
	 */
	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("GET /api/orders should return 200 and a JSON array of orders")
	void getAll_shouldReturnListOfOrders() throws Exception {
		// Arrange: two fake orders returned by the mocked service
		OrderDto first = new OrderDto();
		first.setId(1L);
		first.setCode("ORD-1");
		first.setStatus(OrderStatus.NEW);
		first.setTotal(BigDecimal.valueOf(10.00));

		OrderDto second = new OrderDto();
		second.setId(2L);
		second.setCode("ORD-2");
		second.setStatus(OrderStatus.PAID); // ensure this status exists in your enum
		second.setTotal(BigDecimal.valueOf(20.50));

		when(orderService.findAll()).thenReturn(List.of(first, second));

		// Act + Assert: call GET /api/orders and verify the result
		mockMvc.perform(get("/api/orders").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				// Check array size and a couple of fields for each element
				.andExpect(jsonPath("$.length()").value(2)).andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].code").value("ORD-1")).andExpect(jsonPath("$[0].status").value("NEW"))
				.andExpect(jsonPath("$[0].total").value(10.00)).andExpect(jsonPath("$[1].id").value(2))
				.andExpect(jsonPath("$[1].code").value("ORD-2")).andExpect(jsonPath("$[1].status").value("PAID"))
				.andExpect(jsonPath("$[1].total").value(20.50));
	}
}