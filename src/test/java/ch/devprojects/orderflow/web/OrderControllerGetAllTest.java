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
import org.springframework.test.web.servlet.MockMvc;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderService;

/**
 * Web slice test for {@link OrderController} - "list all" endpoint.
 *
 * Focus:
 *  - GET /api/orders
 *  - Ensure we get HTTP 200 and a JSON array with the expected elements.
 *
 *  Keep each test class small and focused (single responsibility).
 *  Increase coverage of the "list all" path without touching the database.
 */
@WebMvcTest(OrderController.class)
class OrderControllerGetAllTest {

    /**
     * The controller depends on OrderService. Here we mock it so that
     * we can fully control the returned data and keep the test fast.
     */

    @Autowired
    private OrderService orderService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/orders should return 200 and a JSON array of orders")
    void getAll_shouldReturnListOfOrders() throws Exception {
        // Arrange: two fake orders from the service layer
        OrderDto first = new OrderDto();
        first.setId(1L);
        first.setCode("ORD-1");
        first.setStatus(OrderStatus.NEW);
        first.setTotal(BigDecimal.valueOf(10.00));

        OrderDto second = new OrderDto();
        second.setId(2L);
        second.setCode("ORD-2");
        second.setStatus(OrderStatus.PAID);
        second.setTotal(BigDecimal.valueOf(20.50));

        when(orderService.findAll()).thenReturn(List.of(first, second));

        // Act + Assert: call GET /api/orders and verify the result
        mockMvc.perform(get("/api/orders")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // Check array size and a couple of fields for each element
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("ORD-1"))
                .andExpect(jsonPath("$[0].status").value("NEW"))
                .andExpect(jsonPath("$[0].total").value(10.00))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].code").value("ORD-2"))
                .andExpect(jsonPath("$[1].status").value("PAID"))
                .andExpect(jsonPath("$[1].total").value(20.50));
    }
}