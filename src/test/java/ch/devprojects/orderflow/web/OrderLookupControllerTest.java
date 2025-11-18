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
import org.springframework.test.web.servlet.MockMvc;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderService;
import jakarta.persistence.EntityNotFoundException;

/**
 * Web slice tests for {@link OrderLookupController}.
 *
 * Focus: - Verify that GET /api/orders/by-code/{code}: * delegates to
 * OrderService.findByCode(...) * returns 200 OK with the expected JSON payload
 * when the order exists * returns 404 NOT FOUND when the service throws
 * EntityNotFoundException
 */
@WebMvcTest(OrderLookupController.class)
class OrderLookupControllerTest {

    @Autowired
    private OrderService orderService;
    
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
		// Details of the error body are handled/covered by GlobalExceptionHandlerTest
	}
}