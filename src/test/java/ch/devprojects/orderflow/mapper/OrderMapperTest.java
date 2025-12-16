package ch.devprojects.orderflow.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;

class OrderMapperTest {

	private final OrderMapper mapper = new OrderMapper();

	@Test
	void toDto_shouldMapStatusAsString() {
		Order entity = new Order();
		entity.setId(1L);
		entity.setCode("ORD-1");
		entity.setStatus(OrderStatus.PAID);
		entity.setTotal(BigDecimal.TEN);

		OrderDto dto = mapper.toDto(entity);

		// DTO contract: status is String
		assertEquals("PAID", dto.getStatus());
	}

	@Test
	void toEntity_shouldParseStatusStringToEnum() {
		OrderDto dto = new OrderDto();
		dto.setCode("ORD-2");
		dto.setStatus("SHIPPED");
		dto.setTotal(BigDecimal.valueOf(20));

		Order entity = mapper.toEntity(dto);

		assertEquals(OrderStatus.SHIPPED, entity.getStatus());
	}

	@Test
	void updateEntityFromDto_shouldUpdateStatusFromString() {
		Order entity = new Order();
		entity.setStatus(OrderStatus.NEW);

		OrderDto dto = new OrderDto();
		dto.setStatus("PROCESSING");

		mapper.updateEntityFromDto(dto, entity);

		assertEquals(OrderStatus.PROCESSING, entity.getStatus());
	}
}