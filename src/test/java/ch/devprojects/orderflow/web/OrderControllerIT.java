package ch.devprojects.orderflow.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // <-- ensure H2 in-memory + Flyway runs
class OrderControllerIT {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void createOrder_shouldReturn201_andPersist() throws Exception {
		OrderDto dto = new OrderDto();
		dto.setCode("ORD-1001");
		dto.setStatus(OrderStatus.NEW);
		dto.setTotal(BigDecimal.valueOf(99.99));

		mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code").value("ORD-1001")).andExpect(jsonPath("$.status").value("NEW"));
	}
}