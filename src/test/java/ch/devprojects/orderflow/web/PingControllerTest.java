package ch.devprojects.orderflow.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false) // disable security filters for these tests
class PingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void ping_shouldReturnExactAliveMessage() throws Exception {
		mockMvc.perform(get("/api/ping")).andExpect(status().isOk())
				.andExpect(content().string("OrderFlow API is alive - running..."));
	}

	@Test
	void pong_shouldReturnExactPongMessage() throws Exception {
		mockMvc.perform(get("/api/ping/pong")).andExpect(status().isOk()).andExpect(content().string("pong"));
	}

	@Test
	void pingTime_shouldReturnTimestampPrefix() throws Exception {
		mockMvc.perform(get("/api/ping/time")).andExpect(status().isOk())
				.andExpect(content().string(startsWith("ping_the_endpoint_time@")));
	}
}