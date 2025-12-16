package ch.devprojects.orderflow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;
import ch.devprojects.orderflow.dto.OrderDto;

/**
 * Unit tests for {@link OrderServiceImpl}.
 *
 * Critical detail: - {@link OrderServiceImpl} constructor requires a NON-null
 * mapper: ch.devprojects.orderflow.mapper.OrderMapper
 *
 * If you have multiple OrderMapper classes in different packages, make sure
 * THIS test imports and mocks the correct one, otherwise Mockito won't inject
 * it and you'll get: "orderMapper must not be null".
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

	@Mock
	private ch.devprojects.orderflow.repository.OrderRepository orderRepository;

	/**
	 * IMPORTANT: mock the mapper type that OrderServiceImpl actually depends on:
	 * ch.devprojects.orderflow.mapper.OrderMapper
	 *
	 * Using a different OrderMapper from another package will NOT be injected.
	 */
	@Mock
	private ch.devprojects.orderflow.mapper.OrderMapper orderMapper;

	@InjectMocks
	private OrderServiceImpl orderService;

	@Test
	@DisplayName("create should map dto -> entity, save it, and return dto")
	void create_shouldSetDefaultsAndSave() {
		// Arrange
		OrderDto input = new OrderDto();
		input.setCode("ORD-1");
		input.setStatus("NEW");
		input.setTotal(new BigDecimal("10.00"));
		input.setCustomerName("Alice");
		input.setDescription("My description");

		Order mappedEntity = new Order();
		Order savedEntity = new Order();

		OrderDto mappedBack = new OrderDto();
		mappedBack.setId(1L);
		mappedBack.setCode("ORD-1");
		mappedBack.setStatus("NEW");
		mappedBack.setTotal(new BigDecimal("10.00"));
		mappedBack.setCustomerName("Alice");
		mappedBack.setDescription("My description");

		when(orderMapper.toEntityForCreate(input)).thenReturn(mappedEntity);
		when(orderRepository.save(mappedEntity)).thenReturn(savedEntity);
		when(orderMapper.toDto(savedEntity)).thenReturn(mappedBack);

		// Act
		OrderDto result = orderService.create(input);

		// Assert
		assertEquals(1L, result.getId());
		assertEquals("ORD-1", result.getCode());
		assertEquals("My description", result.getDescription());

		verify(orderMapper, times(1)).toEntityForCreate(input);
		verify(orderRepository, times(1)).save(mappedEntity);
		verify(orderMapper, times(1)).toDto(savedEntity);

		verifyNoMoreInteractions(orderRepository, orderMapper);
	}

	@Test
	@DisplayName("findByCode should return order when it exists")
	void findByCode_shouldReturnOrder_whenExists() {
		// Arrange
		Order entity = new Order();
		entity.setId(10L);
		entity.setCode("ORD-10");
		entity.setStatus(OrderStatus.PAID);
		entity.setTotal(new BigDecimal("99.90"));
		entity.setDescription("Paid order");

		OrderDto dto = new OrderDto();
		dto.setId(10L);
		dto.setCode("ORD-10");
		dto.setStatus("PAID");
		dto.setTotal(new BigDecimal("99.90"));
		dto.setDescription("Paid order");

		when(orderRepository.findByCode("ORD-10")).thenReturn(Optional.of(entity));
		when(orderMapper.toDto(entity)).thenReturn(dto);

		// Act
		OrderDto result = orderService.findByCode("ORD-10");

		// Assert
		assertEquals(10L, result.getId());
		assertEquals("ORD-10", result.getCode());
		assertEquals("Paid order", result.getDescription());

		verify(orderRepository, times(1)).findByCode("ORD-10");
		verify(orderMapper, times(1)).toDto(entity);
		verifyNoMoreInteractions(orderRepository, orderMapper);
	}

	@Test
	@DisplayName("findByCode should throw when order does not exist")
	void findByCode_shouldThrow_whenNotFound() {
		// Arrange
		when(orderRepository.findByCode("MISSING")).thenReturn(Optional.empty());

		// Act + Assert
		assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> orderService.findByCode("MISSING"));

		verify(orderRepository, times(1)).findByCode("MISSING");
		verifyNoMoreInteractions(orderRepository);
		verifyNoInteractions(orderMapper);
	}

	@Test
	@DisplayName("search without filters should delegate to repository.findAll(spec)")
	void search_withoutFilters_shouldDelegateToRepository() {
		// Arrange
		Order e1 = new Order();
		Order e2 = new Order();

		OrderDto d1 = new OrderDto();
		OrderDto d2 = new OrderDto();

		when(orderRepository.findAll(any(Specification.class))).thenReturn(List.of(e1, e2));
		when(orderMapper.toDto(e1)).thenReturn(d1);
		when(orderMapper.toDto(e2)).thenReturn(d2);

		// Act
		List<OrderDto> result = orderService.search(null, null);

		// Assert
		assertEquals(2, result.size());

		verify(orderRepository, times(1)).findAll(any(Specification.class));
		verify(orderMapper, times(1)).toDto(e1);
		verify(orderMapper, times(1)).toDto(e2);
		verifyNoMoreInteractions(orderRepository, orderMapper);
	}

	@Test
	@DisplayName("search with code + status should still delegate to repository.findAll(spec)")
	void search_withCodeAndStatus_shouldDelegateToRepository() {
		// Arrange
		Order e1 = new Order();
		OrderDto d1 = new OrderDto();

		when(orderRepository.findAll(any(Specification.class))).thenReturn(List.of(e1));
		when(orderMapper.toDto(e1)).thenReturn(d1);

		// Act
		List<OrderDto> result = orderService.search("ORD", OrderStatus.NEW);

		// Assert
		assertEquals(1, result.size());

		verify(orderRepository, times(1)).findAll(any(Specification.class));
		verify(orderMapper, times(1)).toDto(e1);
		verifyNoMoreInteractions(orderRepository, orderMapper);
	}
}