package ch.devprojects.orderflow.web;

import static org.mockito.ArgumentMatchers.eq;
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
import jakarta.persistence.EntityNotFoundException;

/**
 * Web slice tests for {@link OrderLookupController}.
 *
 * <p>
 * Focus:
 * </p>
 * <ul>
 * <li>Verify that GET /api/orders/by-code/{code}:</li>
 * <li>delegates to {@link OrderService#findByCode(String)}</li>
 * <li>returns 200 OK with the expected JSON payload when the order exists</li>
 * <li>returns 404 Not Found when the service throws
 * {@link EntityNotFoundException}</li>
 * </ul>
 *
 * <p>
 * The test exercises only the web layer. The {@link OrderService} is replaced
 * by a Mockito-based mock, so we can precisely control the behaviour without
 * touching the database.
 * </p>
 */
@WebMvcTest(OrderLookupController.class)
class OrderLookupControllerTest {

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

	// -----------------------------
	// Happy path
	// -----------------------------
	@Test
	@DisplayName("GET /api/orders/by-code/{code} should return 200 and the Order JSON when the order exists")
	void getByCode_shouldReturnOrder_whenExists() throws Exception {
		// Arrange
		String code = "ORD-XYZ";

		OrderDto dto = new OrderDto();
		dto.setId(42L);
		dto.setCode(code);
		dto.setStatus(OrderStatus.NEW);
		dto.setTotal(BigDecimal.valueOf(123.45));

		when(orderService.findByCode(eq(code))).thenReturn(dto);

		// Act + Assert
		mockMvc.perform(get("/api/orders/by-code/{code}", code).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(42)).andExpect(jsonPath("$.code").value("ORD-XYZ"))
				.andExpect(jsonPath("$.status").value("NEW")).andExpect(jsonPath("$.total").value(123.45));
	}

	// -----------------------------
	// Not found path
	// -----------------------------
	@Test
	@DisplayName("GET /api/orders/by-code/{code} should return 404 when the order does not exist")
	void getByCode_shouldReturn404_whenMissing() throws Exception {
		// Arrange
		String code = "MISSING";

		when(orderService.findByCode(eq(code))).thenThrow(new EntityNotFoundException("Order not found: " + code));

		// Act + Assert
		mockMvc.perform(get("/api/orders/by-code/{code}", code).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		// Details of the error body are covered by GlobalExceptionHandler tests.
	}
}