package ch.devprojects.orderflow.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the global CORS configuration.
 *
 * Current goal: - Ensure that our *intended* frontends are allowed and receive
 * a proper Access-Control-Allow-Origin header on /api/** endpoints.
 *
 * We do NOT assert any specific behaviour for "unknown" origins here anymore.
 * The runtime behaviour may allow them (public API), and the browser's CORS
 * enforcement is sufficient to protect credentials, given that the API is
 * stateless and allowCredentials(false) is used.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CorsConfigurationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("CORS - localhost:4200 is allowed on /api/** and gets Access-Control-Allow-Origin")
	void cors_allowsLocalhost4200OnApiEndpoints() throws Exception {
		mockMvc.perform(get("/api/orders").header("Origin", "http://localhost:4200")).andExpect(status().isOk())
				.andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
	}

	@Test
	@DisplayName("CORS - https://devprojects.ch is allowed on /api/** and gets Access-Control-Allow-Origin")
	void cors_allowsDevprojectsOnApiEndpoints() throws Exception {
		mockMvc.perform(get("/api/orders").header("Origin", "https://devprojects.ch")).andExpect(status().isOk())
				.andExpect(header().string("Access-Control-Allow-Origin", "https://devprojects.ch"));
	}
}