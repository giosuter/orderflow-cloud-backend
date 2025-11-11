package ch.devprojects.orderflow.repository;

import ch.devprojects.orderflow.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
	Optional<Order> findByCode(String code);

	boolean existsByCode(String code);
}