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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderService;

/**
 * Web slice test for {@link OrderController} - lookup by code.
 *
 * <p>
 * Focus:
 * </p>
 * <ul>
 * <li>GET /api/orders/code/{code}</li>
 * <li>Verify that the controller returns a single {@link OrderDto} with the
 * expected fields.</li>
 * </ul>
 *
 * <p>
 * This test exercises only the web layer. The {@link OrderService} is replaced
 * by a Mockito-based mock, which allows us to control the behaviour of the
 * business logic without hitting the database.
 * </p>
 */
@WebMvcTest(OrderController.class)
class OrderControllerGetByCodeTest {

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
	 * full Spring MVC stack.
	 */
	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("GET /api/orders/code/{code} returns OrderDto")
	void getByCode_shouldReturnOrder_whenExists() throws Exception {
		// Arrange: prepare a fake OrderDto returned by the mocked service
		OrderDto dto = new OrderDto();
		dto.setId(7L);
		dto.setCode("XYZ777");
		dto.setStatus(OrderStatus.NEW);
		dto.setTotal(BigDecimal.TEN);

		when(orderService.findByCode("XYZ777")).thenReturn(dto);

		// Act + Assert: call the endpoint and check the JSON response
		mockMvc.perform(get("/api/orders/code/{code}", "XYZ777").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(7))
				.andExpect(jsonPath("$.code").value("XYZ777")).andExpect(jsonPath("$.status").value("NEW"))
				.andExpect(jsonPath("$.total").value(10));
	}
}