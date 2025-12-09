package ch.devprojects.orderflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global CORS configuration for the OrderFlow API.
 *
 * - Applies to all endpoints under /api/** - Allows ANY Origin (including
 * "unknown" ones). - Allows standard REST methods and all headers. - Does NOT
 * use cookies / browser-managed credentials for now.
 *
 * When everything is stable in production, you can later tighten this (for
 * example by restricting allowedOriginPatterns to specific domains).
 */
@Configuration
public class WebCorsConfig {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {

			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**")
						// Allow ANY Origin
						.allowedOriginPatterns("*")
						// Standard HTTP methods (including preflight)
						.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
						// Allow all headers from the client
						.allowedHeaders("*")
						// Expose useful headers
						.exposedHeaders("Location")
						// No cookies / browser credentials for now
						.allowCredentials(false)
						// Cache preflight responses for 1 hour
						.maxAge(3600);
			}
		};
	}
}