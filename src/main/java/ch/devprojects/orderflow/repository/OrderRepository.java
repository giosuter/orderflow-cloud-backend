package ch.devprojects.orderflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;

/**
 * Repository for Order persistence and query access.
 *
 * Notes: - JpaRepository gives CRUD + paging/sorting basics. -
 * JpaSpecificationExecutor allows dynamic filtering via Specifications. -
 * Derived query methods (like findByCodeIgnoreCase) are built from field names.
 */
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

	/**
	 * Find an order by its business code (case-insensitive).
	 */
	Optional<Order> findByCodeIgnoreCase(String code);

	/**
	 * Find all orders by their status.
	 *
	 * Important: - This method name must match the exact field name in Order. - It
	 * will work if your Order entity has a field named: "status" of type
	 * OrderStatus (or compatible).
	 */
	List<Order> findByStatus(OrderStatus status);
}