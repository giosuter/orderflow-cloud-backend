package ch.devprojects.orderflow.web;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration-style MVC tests for PingController using MockMvc. We verify
 * stable semantics rather than brittle exact strings.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class PingControllerTest {

	@Autowired
	private MockMvc mvc;

	@Test
	@DisplayName("GET /api/ping -> starts with stable alive message")
	void ping_shouldReturnAliveMessagePing() throws Exception {
		mvc.perform(get("/api/ping")).andExpect(status().isOk())
				.andExpect(content().string(startsWith("OrderFlow API is alive")));
	}

	@Test
	@DisplayName("GET /api/ping/time -> starts with 'ping_the_endpoint_time@'")
	void pingTime_shouldReturnPongWithTimestampPrefix() throws Exception {
		mvc.perform(get("/api/ping/time")).andExpect(status().isOk())
				.andExpect(content().string(startsWith("ping_the_endpoint_time@")));
	}

	@Test
	@DisplayName("GET /api/ping -> not the trivial 'Pong'")
	void ping_shouldReturnAliveMessagePong() throws Exception {
		mvc.perform(get("/api/ping")).andExpect(status().isOk()).andExpect(content().string(not("Pong")));
	}
}