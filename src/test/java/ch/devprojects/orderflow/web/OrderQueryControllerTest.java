package ch.devprojects.orderflow.web;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderResponseDto;
import ch.devprojects.orderflow.dto.OrdersPageResponse;
import ch.devprojects.orderflow.service.OrderQueryService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MVC slice test for {@link OrderQueryController}.
 *
 * Verifies: - Request parameters are forwarded correctly to the service -
 * Response JSON structure matches the API contract
 */
@WebMvcTest(OrderQueryController.class)
class OrderQueryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderQueryService orderQueryService;

	@Test
	@DisplayName("GET /api/orders/search forwards paging + sorting and returns OrdersPageResponse")
	void search_shouldForwardParamsAndReturnResponse() throws Exception {
		// Arrange
		OrderResponseDto dto = new OrderResponseDto();
		dto.setId(1L);
		dto.setCode("ORD-001");
		dto.setStatus("NEW");
		dto.setCustomerName("Alice");
		dto.setTotal(BigDecimal.valueOf(123.45));
		dto.setCreatedAt(LocalDateTime.of(2025, 12, 14, 10, 0));

		OrdersPageResponse response = new OrdersPageResponse();
		response.setContent(List.of(dto));
		response.setPage(0);
		response.setSize(20);
		response.setTotalElements(1);
		response.setTotalPages(1);
		response.setFirst(true);
		response.setLast(true);

		when(orderQueryService.findOrders(eq("alice"), eq(OrderStatus.NEW), eq(0), eq(20), eq("code"), eq("asc")))
				.thenReturn(response);

		// Act + Assert
		mockMvc.perform(get("/api/orders/search").param("customer", "alice").param("status", "NEW").param("page", "0")
				.param("size", "20").param("sortBy", "code").param("sortDir", "asc").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(1))
				.andExpect(jsonPath("$.content[0].code").value("ORD-001"))
				.andExpect(jsonPath("$.content[0].status").value("NEW")).andExpect(jsonPath("$.page").value(0))
				.andExpect(jsonPath("$.size").value(20)).andExpect(jsonPath("$.totalElements").value(1))
				.andExpect(jsonPath("$.totalPages").value(1)).andExpect(jsonPath("$.first").value(true))
				.andExpect(jsonPath("$.last").value(true));

		// Verify forwarding
		verify(orderQueryService).findOrders("alice", OrderStatus.NEW, 0, 20, "code", "asc");
	}
}