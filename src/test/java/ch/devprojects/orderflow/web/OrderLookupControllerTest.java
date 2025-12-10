package ch.devprojects.orderflow.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ch.devprojects.orderflow.dto.OrderResponseDto;
import ch.devprojects.orderflow.service.OrderLookupService;

/**
 * Pure unit test for {@link OrderLookupController}.
 *
 * This test: - Instantiates the controller with a mocked
 * {@link OrderLookupService} - Verifies that the controller correctly
 * translates service results into HTTP responses (200 OK / 404 NOT_FOUND)
 *
 * No Spring context is started here; we only use the Spring HTTP types for the
 * ResponseEntity, everything else is a plain JUnit + Mockito test.
 */
@ExtendWith(MockitoExtension.class)
class OrderLookupControllerTest {

	@Mock
	private OrderLookupService orderLookupService;

	@InjectMocks
	private OrderLookupController controller;

	@Test
	@DisplayName("getOrderById returns 200 OK and body when order exists")
	void getOrderById_returnsOkWithBody() {
		// Arrange
		Long orderId = 1L;

		OrderResponseDto dto = new OrderResponseDto();
		dto.setId(orderId);
		dto.setCode("ORD-2025-0001");
		dto.setStatus("NEW");
		dto.setCustomerName("Acme GmbH");
		dto.setAssignedTo("Giovanni Suter");
		dto.setTotal(new BigDecimal("120.50"));
		dto.setCreatedAt(LocalDateTime.now().minusDays(1));

		when(orderLookupService.findById(orderId)).thenReturn(Optional.of(dto));

		// Act
		ResponseEntity<OrderResponseDto> response = controller.getOrderById(orderId);

		// Assert
		assertNotNull(response, "Response must not be null");
		assertEquals(HttpStatus.OK, response.getStatusCode(), "Status must be 200 OK");
		assertNotNull(response.getBody(), "Body must not be null");
		assertEquals(orderId, response.getBody().getId(), "Returned order ID must match requested ID");

		// Verify delegation to the service
		verify(orderLookupService).findById(orderId);
	}

	@Test
	@DisplayName("getOrderById returns 404 NOT_FOUND when order does not exist")
	void getOrderById_returnsNotFoundWhenMissing() {
		// Arrange
		Long missingId = 999L;
		when(orderLookupService.findById(missingId)).thenReturn(Optional.empty());

		// Act
		ResponseEntity<OrderResponseDto> response = controller.getOrderById(missingId);

		// Assert
		assertNotNull(response, "Response must not be null");
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Status must be 404 NOT_FOUND");
		// For 404 we expect an empty body
		assertEquals(null, response.getBody(), "Body must be null for NOT_FOUND");

		// Verify delegation to the service
		verify(orderLookupService).findById(missingId);
	}
}