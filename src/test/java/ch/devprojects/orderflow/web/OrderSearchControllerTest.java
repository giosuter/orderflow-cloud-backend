package ch.devprojects.orderflow.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderService;

/**
 * Web slice test for OrderSearchController.
 *
 * Focus:
 *  - GET /api/orders/search
 *  - Ensures that query params are passed to OrderService.search(...)
 *    and JSON array is returned correctly.
 */
@WebMvcTest(OrderSearchController.class)
class OrderSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("GET /api/orders/search should return matching orders as JSON")
    void search_shouldReturnMatchingOrders() throws Exception {
        // Arrange: mock service response
        OrderDto dto = new OrderDto();
        dto.setId(1L);
        dto.setCode("ORD-1");
        dto.setStatus(OrderStatus.NEW);
        dto.setTotal(BigDecimal.valueOf(99.90));

        when(orderService.search("ORD", OrderStatus.NEW))
                .thenReturn(List.of(dto));

        // Act + Assert
        mockMvc.perform(get("/api/orders/search")
                    .param("code", "ORD")
                    .param("status", "NEW")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("ORD-1"))
                .andExpect(jsonPath("$[0].status").value("NEW"))
                .andExpect(jsonPath("$[0].total").value(99.90));
    }
}