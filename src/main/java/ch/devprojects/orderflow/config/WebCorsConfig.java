package ch.devprojects.orderflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global CORS configuration for the OrderFlow API.
 *
 * Rules: 
 * - Only paths under /api/** are CORS-enabled.
 * - Allowed origins: 
 *   - http://localhost:4200 (Angular dev server) 
 *   - https://devprojects.ch (Angular app in production) 
 * - Allowed methods: typical REST verbs + OPTIONS.
 * - Allowed headers: all, so Angular can send JSON, auth headers, etc. 
 * - Credentials are disabled for now (stateless API; no cookies).
 */
@Configuration
public class WebCorsConfig {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {

			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**")
						// Only allow your known frontends
						.allowedOrigins("http://localhost:4200", "https://devprojects.ch")
						// Standard REST verbs + preflight
						.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
						// Allow all headers from the frontend
						.allowedHeaders("*")
						// Optionally expose some headers (e.g. Location on POST)
						.exposedHeaders("Location")
						// No cookies / auth via browser-managed credentials for now
						.allowCredentials(false)
						// Cache preflight response for 1 hour
						.maxAge(3600);
			}
		};
	}
}