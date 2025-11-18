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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import ch.devprojects.orderflow.service.OrderService;

/**
 * Web slice tests for the DELETE endpoint in {@link OrderController}.
 *
 * This test must:
 *  - protect the REST contract of DELETE /api/orders/{id}.
 *  - verify that:
 *      * a successful deletion returns 204 No Content
 *      * a missing order results in 404 Not Found with our standardized
 *        error payload from GlobalExceptionHandler.
 *
 * Scope:
 *  - No database, no Flyway. We mock OrderService.
 */
@WebMvcTest(OrderController.class)
class OrderControllerDeleteTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * The controller depends on OrderService; here we mock it 
     * so we can simulate success and failure scenarios very quickly.
     */
    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("DELETE /api/orders/{id} should return 204 when deletion succeeds")
    void deleteExistingOrder_shouldReturn204() throws Exception {
        // Arrange
        Long id = 42L;
        // Deletion succeeds, i.e. no exception thrown by the service.
        doNothing().when(orderService).delete(id);

        // Act + Assert
        mockMvc.perform(delete("/api/orders/{id}", id))
                .andExpect(status().isNoContent());

        // Ensure the controller actually called the service once
        verify(orderService, times(1)).delete(id);
    }

    @Test
    @DisplayName("DELETE /api/orders/{id} should return 404 when order does not exist")
    void deleteNonExistingOrder_shouldReturn404() throws Exception {
        // Arrange
        Long missingId = 99L;
        // Simulate service behavior for non-existing entity
        doThrow(new EntityNotFoundException("Order not found: " + missingId))
                .when(orderService).delete(missingId);

        // Act + Assert: we expect our GlobalExceptionHandler to kick in
        mockMvc.perform(delete("/api/orders/{id}", missingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Order not found: " + missingId))
                .andExpect(jsonPath("$.path").value("/api/orders/" + missingId));
    }
}