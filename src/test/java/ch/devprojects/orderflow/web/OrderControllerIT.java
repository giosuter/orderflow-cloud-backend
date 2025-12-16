package ch.devprojects.orderflow.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerIT {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void createAndUpdateOrder_shouldWork() {
		OrderDto dto = new OrderDto();
		dto.setCode("ORD-IT");
		dto.setStatus("NEW"); // FIXED
		dto.setTotal(BigDecimal.TEN);

		ResponseEntity<OrderDto> created = restTemplate.postForEntity("/api/orders", dto, OrderDto.class);

		assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(created.getBody().getStatus()).isEqualTo("NEW");

		OrderDto updateDto = created.getBody();
		updateDto.setStatus("PAID"); // FIXED

		restTemplate.put("/api/orders/{id}", updateDto, updateDto.getId());
	}
}