package ch.devprojects.orderflow.service;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Minimal JPA configuration for @DataJpaTest in the "service" test package.
 *
 * Why:
 * - @DataJpaTest limits the loaded context to JPA components.
 * - By default it scans from the test package; here we explicitly
 *   point it to the main application packages for entities & repos.
 */
@Configuration
@EntityScan(basePackages = "ch.devprojects.orderflow.domain")
@EnableJpaRepositories(basePackages = "ch.devprojects.orderflow.repository")
public class TestJpaConfig {
    // No beans needed; annotations are enough.
}