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
 * Goal:
 * - Ensure that only the configured origins get CORS headers on /api/** endpoints.
 * - Use /api/ping as a simple, always-available endpoint.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CorsConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("CORS - allowed origin http://localhost:4200 should receive Access-Control-Allow-Origin header")
    void cors_allowsLocalhost4200OnApiEndpoints() throws Exception {
        mockMvc.perform(get("/api/ping")
                        .header("Origin", "http://localhost:4200"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    @DisplayName("CORS - allowed origin https://devprojects.ch should receive Access-Control-Allow-Origin header")
    void cors_allowsDevprojectsOnApiEndpoints() throws Exception {
        mockMvc.perform(get("/api/ping")
                        .header("Origin", "https://devprojects.ch"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://devprojects.ch"));
    }

    @Test
    @DisplayName("CORS - unknown origin should be rejected with 403 and no Access-Control-Allow-Origin header")
    void cors_blocksUnknownOriginsOnApiEndpoints() throws Exception {
        mockMvc.perform(get("/api/ping")
                        .header("Origin", "http://evil.example.com"))
                // Spring treats disallowed origins as forbidden, which is fine.
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}