package ch.devprojects.orderflow;

import org.junit.jupiter.api.Test;

/**
 * Basic smoke test for the main Spring Boot entry point.
 * By calling the main method directly, we verify that the application
 * can start without throwing errors in the test environment.
 * If there is a misconfiguration in Spring Boot startup, this test will fail.
 */
class OrderflowCloudBackendApplicationTest {

    @Test
    void main_shouldStartApplicationWithoutErrors() {
        // Arrange
        String[] args = new String[]{}; // no CLI arguments

        // Act & Assert
        // If the application fails to start (e.g., broken configuration),
        // this call will throw an exception and the test will fail.
        OrderflowCloudBackendApplication.main(args);
    }
}