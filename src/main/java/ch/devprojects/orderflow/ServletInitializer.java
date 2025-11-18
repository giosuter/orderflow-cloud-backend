package ch.devprojects.orderflow;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * ServletInitializer is used when deploying the application as a WAR
 * to an external servlet container (Tomcat on Hostpoint).
 *
 * It tells Spring Boot which main configuration class to use when the
 * container starts the app.
 */
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        // Register the Spring Boot application class as a configuration source.
        // This is what external Tomcat (Hostpoint) will use for WAR startup.
        return application.sources(OrderflowCloudBackendApplication.class);
    }
}