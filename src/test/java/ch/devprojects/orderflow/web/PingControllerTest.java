package ch.devprojects.orderflow.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PingController.class)
class PingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void ping_shouldReturnAliveMessage() throws Exception {
		// With @WebMvcTest, MockMvc uses the controller mappings only, no server
		// context-path.
		mockMvc.perform(get("/api/ping")).andExpect(status().isOk())
				.andExpect(content().string("OrderFlow API is alive"));
	}
}