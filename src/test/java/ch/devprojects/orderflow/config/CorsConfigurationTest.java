package ch.devprojects.orderflow.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

/**
 * CORS integration tests for the current "allow-all" configuration.
 *
 * For the debugging phase we configured {@link WebCorsConfig} so that: - all
 * origins are allowed - all methods are allowed - /api/** is CORS-enabled
 *
 * This test checks that a request from an arbitrary Origin to /api/orders: - is
 * accepted (HTTP 200) - echoes the Origin in Access-Control-Allow-Origin.
 *
 * IMPORTANT: When you later tighten CORS again, update this test accordingly.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CorsConfigurationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("CORS allows requests from any Origin on /api/** and echoes Origin header")
	void cors_allowsUnknownOriginOnApiEndpoints() throws Exception {
		String origin = "https://some-unknown-origin.example";

		mockMvc.perform(get("/api/orders").header(HttpHeaders.ORIGIN, origin)).andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin));
	}
}