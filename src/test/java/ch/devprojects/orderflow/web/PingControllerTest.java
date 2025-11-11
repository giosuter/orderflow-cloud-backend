package ch.devprojects.orderflow.web;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest // full context
@AutoConfigureMockMvc // creates MockMvc bean
class PingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void ping_shouldReturnAliveMessagePing() throws Exception {
		// With @WebMvcTest, MockMvc uses the controller mappings only, no server
		// context-path.
		mockMvc.perform(get("/api/ping")).andExpect(status().isOk())
				.andExpect(content().string("OrderFlow API is alive - Ping Pang Peng"));
	}
	
	@Test
	void ping_shouldReturnAliveMessagePong() throws Exception {
		// With @WebMvcTest, MockMvc uses the controller mappings only, no server
		// context-path.
		mockMvc.perform(get("/api/pong")).andExpect(status().isOk())
				.andExpect(content().string("OrderFlow API is alive - Pong"));
	}
	
    @Test
    void pingTime_shouldReturnPongWithTimestampPrefix() throws Exception {
        mockMvc.perform(get("/api/ping/time"))
               .andExpect(status().isOk())
               .andExpect(content().string(startsWith("ping_the_endpoint_time@")));
    }
}