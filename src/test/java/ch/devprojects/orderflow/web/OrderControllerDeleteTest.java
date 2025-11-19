package ch.devprojects.orderflow.web;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ch.devprojects.orderflow.service.OrderService;

/**
 * Web slice tests for the DELETE endpoint of {@link OrderController}.
 *
 * <p>
 * Focus:
 * </p>
 * <ul>
 * <li>DELETE /api/orders/{id}</li>
 * <li>Verify HTTP 204 for existing orders</li>
 * <li>Verify HTTP 404 and error payload for missing orders</li>
 * </ul>
 *
 * <p>
 * These tests exercise only the web layer. The {@link OrderService} is replaced
 * by a Mockito-based mock so that we can fully control the behaviour of the
 * business logic without touching the database.
 * </p>
 */
@WebMvcTest(OrderController.class)
class OrderControllerDeleteTest {

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
	 * full servlet container. This keeps the tests fast while still exercising the
	 * full Spring MVC stack (routing, JSON mapping, exception handling, etc.).
	 */
	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("DELETE /api/orders/{id} should return 204 when the order exists")
	void deleteExistingOrder_shouldReturn204() throws Exception {
		// Arrange
		Long existingId = 1L;

		// Simulate successful deletion of an existing order in the service layer
		doNothing().when(orderService).delete(existingId);

		// Act + Assert: perform DELETE and expect HTTP 204 (No Content)
		mockMvc.perform(delete("/api/orders/{id}", existingId)).andExpect(status().isNoContent());

		// Verify that the controller delegated correctly to the service
		verify(orderService, times(1)).delete(existingId);
	}

	@Test
	@DisplayName("DELETE /api/orders/{id} should return 404 with error payload when the order is missing")
	void deleteNonExistingOrder_shouldReturn404() throws Exception {
		// Arrange
		Long missingId = 99L;

		// Simulate service behaviour for a non-existing order:
		// the service throws an EntityNotFoundException, which should be
		// translated by the global exception handler to a 404 response.
		doThrow(new EntityNotFoundException("Order not found: " + missingId)).when(orderService).delete(missingId);

		// Act + Assert: perform DELETE and assert the error response structure
		mockMvc.perform(delete("/api/orders/{id}", missingId).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Not Found"))
				.andExpect(jsonPath("$.message").value("Order not found: " + missingId))
				.andExpect(jsonPath("$.path").value("/api/orders/" + missingId));
	}
}