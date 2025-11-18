package ch.devprojects.orderflow;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Unit test for {@link ServletInitializer}.
 *
 * Goal:
 *  - Ensure that the WAR deployment entry point can configure a
 *    {@link SpringApplicationBuilder} without throwing exceptions.
 *
 * Why this is useful:
 *  - When deployed as a WAR on external Tomcat (Hostpoint),
 *    the container calls this configure(...) method.
 *  - If the method is misconfigured (wrong main class, NPE, etc.),
 *    the app would fail to start at deploy time.
 *
 * We keep this test intentionally simple to avoid depending on
 * Spring Boot internals (like exact content of getSources()).
 */
class ServletInitializerTest {

    @Test
    @DisplayName("ServletInitializer.configure should return the builder without errors")
    void configure_shouldReturnBuilder() {
        // Arrange: fresh builder and initializer (simulates external Tomcat startup)
        ServletInitializer initializer = new ServletInitializer();
        SpringApplicationBuilder builder = new SpringApplicationBuilder();

        // Act: let the initializer configure the builder
        SpringApplicationBuilder configured = initializer.configure(builder);

        // Assert: builder is not null and we get back the same instance.
        // If configure(...) misbehaves, this test will fail.
        assertNotNull(configured, "Configured SpringApplicationBuilder must not be null");
        assertSame(builder, configured, "configure(...) should return the same builder instance");
    }
}