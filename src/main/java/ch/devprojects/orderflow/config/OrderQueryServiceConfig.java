package ch.devprojects.orderflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.devprojects.orderflow.repository.OrderRepository;
import ch.devprojects.orderflow.service.OrderQueryService;
import ch.devprojects.orderflow.service.OrderQueryServiceImpl;

/**
 * Explicit bean registration for OrderQueryService.
 *
 * Why this exists: - If component scanning is misconfigured or a @Service
 * annotation was removed, the application would fail to start (OrderController
 * requires OrderQueryService). - This configuration guarantees that the
 * OrderQueryService bean exists.
 */
@Configuration
public class OrderQueryServiceConfig {

	@Bean
	public OrderQueryService orderQueryService(OrderRepository orderRepository) {
		return new OrderQueryServiceImpl(orderRepository);
	}
}