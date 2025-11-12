package ch.devprojects.orderflow;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Servlet initializer so the app runs as a WAR on external Tomcat.
 */
public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder app) {
		return app.sources(OrderflowCloudBackendApplication.class);
	}
}