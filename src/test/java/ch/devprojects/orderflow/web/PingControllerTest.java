package ch.devprojects.orderflow.web;


import static ch.devprojects.orderflow.web.PingController.PING_MESSAGE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Lightweight unit test for PingController.
 *
 * Uses standalone MockMvc: no Spring Boot context, no DB, no Flyway, no Spring
 * Security, no SpringDoc – just the controller itself.
 */
class PingControllerTest {

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		// Create a real PingController instance
		PingController controller = new PingController();
		// Build MockMvc around that controller only
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void ping_shouldReturnExactAliveMessage() throws Exception {
		mockMvc.perform(get("/api/ping")).andExpect(status().isOk())
				.andExpect(content().string(PING_MESSAGE));
	}

	@Test
	void pong_shouldReturnExactPongMessage() throws Exception {
		mockMvc.perform(get("/api/ping/pong")).andExpect(status().isOk()).andExpect(content().string("pong"));
	}

	@Test
	void pingTime_shouldReturnTimestampPrefix() throws Exception {
		mockMvc.perform(get("/api/ping/time")).andExpect(status().isOk())
				// We only check the prefix; the timestamp itself is dynamic
				.andExpect(content().string(org.hamcrest.Matchers.startsWith("ping_the_endpoint_time@")));
	}
}