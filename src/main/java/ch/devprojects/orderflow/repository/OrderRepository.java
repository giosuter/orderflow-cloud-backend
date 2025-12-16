package ch.devprojects.orderflow.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;

/**
 * Main JPA repository for Order.
 *
 * Important: - Extends JpaSpecificationExecutor<Order> so OrderQueryServiceImpl
 * can build dynamic filters - Keeps basic finder methods for common use cases
 */
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

	Optional<Order> findByCode(String code);

	Page<Order> findByCustomerNameContainingIgnoreCase(String customerName, Pageable pageable);

	Page<Order> findByStatus(OrderStatus status, Pageable pageable);

	Page<Order> findByCustomerNameContainingIgnoreCaseAndStatus(String customerName, OrderStatus status, Pageable pageable);
}