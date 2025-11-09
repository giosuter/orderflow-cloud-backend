package ch.devprojects.orderflow.web;

import static org.mockito.ArgumentMatchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

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
	
    @Test
    void pingTime_shouldReturnPongWithTimestampPrefix() throws Exception {
        mockMvc.perform(get("/api/ping/time"))
               .andExpect(status().isOk())
               .andExpect(content().string(startsWith("pong@")));
    }
}