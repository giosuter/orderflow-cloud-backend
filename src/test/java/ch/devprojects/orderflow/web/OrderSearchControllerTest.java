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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.service.OrderService;

/**
 * Web slice test for {@link OrderSearchController}.
 *
 * <p>
 * Focus:
 * </p>
 * <ul>
 * <li>GET /api/orders/search</li>
 * <li>Ensure that query parameters are passed to
 * {@link OrderService#search(String, OrderStatus)} and that a JSON array of
 * matching orders is returned correctly.</li>
 * </ul>
 *
 * <p>
 * This test exercises only the web layer. The {@link OrderService} is replaced
 * by a Mockito-based mock, allowing us to control the behaviour of the search
 * logic without touching the database.
 * </p>
 */
@WebMvcTest(OrderSearchController.class)
class OrderSearchControllerTest {

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
	@DisplayName("GET /api/orders/search should return matching orders as JSON")
	void search_shouldReturnMatchingOrders() throws Exception {
		// Arrange: mock service response
		OrderDto dto = new OrderDto();
		dto.setId(1L);
		dto.setCode("ORD-1");
		dto.setStatus(OrderStatus.NEW);
		dto.setTotal(BigDecimal.valueOf(99.90));

		when(orderService.search("ORD", OrderStatus.NEW)).thenReturn(List.of(dto));

		// Act + Assert
		mockMvc.perform(get("/api/orders/search").param("code", "ORD").param("status", "NEW")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].id").value(1)).andExpect(jsonPath("$[0].code").value("ORD-1"))
				.andExpect(jsonPath("$[0].status").value("NEW")).andExpect(jsonPath("$[0].total").value(99.90));
	}
}