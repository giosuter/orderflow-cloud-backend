package ch.devprojects.orderflow.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.repository.OrderRepository;

/**
 * Integration tests for OrderController.
 *
 * This class:
 * - boots the full Spring context (but with an in-memory DB for the "test" profile).
 * - uses MockMvc to hit the real HTTP endpoints (/api/orders/...).
 * - verifies:
 *   - HTTP status codes (201, 200, 400, 404, 204).
 *   - JSON structure for both success and error responses.
 *   - Wiring between controller, service, repository, and GlobalExceptionHandler.
 *
 * Impact on JaCoCo:
 * - Increases coverage for OrderController and GlobalExceptionHandler.
 * - Complements the pure unit tests for OrderServiceImpl.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // ensure test DB + Flyway migrations run
class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // We use the repository only to prepare test data / clean up between tests.
    @Autowired
    private OrderRepository orderRepository;

    /**
     * Clean the database before each test method.
     * This ensures tests are independent and do not influence each other.
     */
    @BeforeEach
    void cleanDatabase() {
        orderRepository.deleteAll();
    }

    // -------------------------------------------------------------------------
    // Helper methods
    // -------------------------------------------------------------------------

    /**
     * Build a simple valid OrderDto for POST/PUT requests.
     */
    private OrderDto buildValidOrderDto(String code) {
        OrderDto dto = new OrderDto();
        dto.setCode(code);
        dto.setStatus(OrderStatus.NEW);
        dto.setTotal(BigDecimal.valueOf(99.99));
        return dto;
    }

    /**
     * Persist an Order entity directly via repository, to be used for GET/PUT/DELETE/search tests.
     */
    private Order persistOrder(String code) {
        Order order = new Order();
        order.setCode(code);
        order.setStatus(OrderStatus.NEW);
        order.setTotal(BigDecimal.valueOf(50.00));
        return orderRepository.save(order);
    }

    // -------------------------------------------------------------------------
    // POST /api/orders
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/orders should return 201 Created and JSON body when request is valid")
    void createOrder_shouldReturn201_andPersist() throws Exception {
        OrderDto dto = buildValidOrderDto("ORD-1001");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.code").value("ORD-1001"))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.total").value(99.99));
    }

    @Test
    @DisplayName("POST /api/orders should return 400 Bad Request and validation errors when code is blank")
    void createOrder_withBlankCode_shouldReturn400() throws Exception {
        OrderDto dto = buildValidOrderDto("   "); // invalid: blank code

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // For MethodArgumentNotValidException, GlobalExceptionHandler returns a body with "errors"
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].field").value("code"));
    }

    @Test
    @DisplayName("POST /api/orders should return 400 Bad Request when total is missing")
    void createOrder_withoutTotal_shouldReturn400() throws Exception {
        OrderDto dto = buildValidOrderDto("ORD-1002");
        dto.setTotal(null); // invalid: @NotNull + @DecimalMin

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    // -------------------------------------------------------------------------
    // GET /api/orders/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/orders/{id} should return 200 OK when order exists")
    void getOrder_existingId_shouldReturn200() throws Exception {
        Order saved = persistOrder("ORD-GET-1");

        mockMvc.perform(get("/api/orders/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.code").value("ORD-GET-1"))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    @DisplayName("GET /api/orders/{id} should return 404 Not Found when order does not exist")
    void getOrder_missingId_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/orders/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    // -------------------------------------------------------------------------
    // GET /api/orders/search
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/orders/search without filters should return all orders")
    void searchOrders_withoutFilters_shouldReturnAll() throws Exception {
        persistOrder("ORD-1");
        persistOrder("ORD-2");

        mockMvc.perform(get("/api/orders/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/orders/search should filter by code substring and status")
    void searchOrders_byCodeAndStatus_shouldReturnFilteredList() throws Exception {
        // Prepare different orders
        persistOrder("ORD-AAA"); // NEW
        persistOrder("ORD-BBB"); // NEW

        Order shipped = persistOrder("ORD-SHIP");
        shipped.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(shipped); // update status

        // Search for SHIPPED orders whose code contains "ORD"
        mockMvc.perform(get("/api/orders/search")
                        .param("code", "ORD")
                        .param("status", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].code").value("ORD-SHIP"))
                .andExpect(jsonPath("$[0].status").value("SHIPPED"));
    }

    // -------------------------------------------------------------------------
    // PUT /api/orders/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("PUT /api/orders/{id} should update an existing order and return 200 OK")
    void updateOrder_existingId_shouldReturn200() throws Exception {
        Order saved = persistOrder("ORD-UPDATE-1");

        OrderDto updateDto = new OrderDto();
        updateDto.setCode("ORD-UPDATED");
        updateDto.setStatus(OrderStatus.PAID);
        updateDto.setTotal(BigDecimal.valueOf(123.45));

        mockMvc.perform(put("/api/orders/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.code").value("ORD-UPDATED"))
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.total").value(123.45));
    }

    @Test
    @DisplayName("PUT /api/orders/{id} should return 404 Not Found when order does not exist")
    void updateOrder_missingId_shouldReturn404() throws Exception {
        OrderDto updateDto = buildValidOrderDto("ORD-ANY");

        mockMvc.perform(put("/api/orders/{id}", 4242L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    // -------------------------------------------------------------------------
    // DELETE /api/orders/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /api/orders/{id} should return 204 No Content when order exists")
    void deleteOrder_existingId_shouldReturn204() throws Exception {
        Order saved = persistOrder("ORD-DEL-1");

        mockMvc.perform(delete("/api/orders/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/orders/{id} should return 404 Not Found when order does not exist")
    void deleteOrder_missingId_shouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/orders/{id}", 5555L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    // -------------------------------------------------------------------------
    // ERROR HANDLING: invalid enum → HttpMessageNotReadableException
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/orders with invalid enum for status should return 400 Bad Request (HttpMessageNotReadableException)")
    void createOrder_withInvalidStatusEnum_shouldReturn400() throws Exception {
        // We deliberately craft JSON with an invalid status string.
        String invalidJson = """
                {
                  "code": "ORD-INVALID-STATUS",
                  "status": "NOT_A_VALID_STATUS",
                  "total": 10.00
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
        // We do not assert the exact message text, because the underlying Jackson
        // error message is version-dependent. The main point is:
        // - 400 Bad Request
        // - Our GlobalExceptionHandler is used (error = "Bad Request").
    }
}