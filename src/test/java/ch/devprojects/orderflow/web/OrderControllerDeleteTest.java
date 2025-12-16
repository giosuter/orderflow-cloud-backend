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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ch.devprojects.orderflow.service.OrderQueryService;
import ch.devprojects.orderflow.service.OrderService;

/**
 * Web slice tests for the DELETE endpoint of {@link OrderController}.
 *
 * Important: - In @WebMvcTest, Spring does NOT load @Service beans. - Therefore
 * every constructor dependency of the controller must be provided as a mock.
 *
 * Controller dependencies: - OrderService - OrderQueryService
 */
@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerDeleteTest {

	@MockitoBean
	private OrderService orderService;

	/**
	 * Required because OrderController also depends on OrderQueryService. Even if
	 * this test doesn't call search endpoints, Spring must be able to instantiate
	 * the controller.
	 */
	@MockitoBean
	private OrderQueryService orderQueryService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("DELETE /api/orders/{id} should return 204 when the order exists")
	void deleteExistingOrder_shouldReturn204() throws Exception {
		Long existingId = 1L;

		doNothing().when(orderService).delete(existingId);

		mockMvc.perform(delete("/api/orders/{id}", existingId)).andExpect(status().isNoContent());

		verify(orderService, times(1)).delete(existingId);
	}

	@Test
	@DisplayName("DELETE /api/orders/{id} should return 404 with error payload when the order is missing")
	void deleteNonExistingOrder_shouldReturn404() throws Exception {
		Long missingId = 99L;

		doThrow(new EntityNotFoundException("Order not found: " + missingId)).when(orderService).delete(missingId);

		mockMvc.perform(delete("/api/orders/{id}", missingId).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Not Found"))
				.andExpect(jsonPath("$.message").value("Order not found: " + missingId))
				.andExpect(jsonPath("$.path").value("/api/orders/" + missingId));
	}
}