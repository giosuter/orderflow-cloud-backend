package ch.devprojects.orderflow.web;

import ch.devprojects.orderflow.dto.OrderDto;
import ch.devprojects.orderflow.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test
 * Full-stack test: controller + service + repo + DB (H2 in dev profile).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("dev")
class OrderControllerIT {

	@Autowired
	MockMvc mvc;
	@Autowired
	ObjectMapper om;
	@Autowired
	OrderRepository repo;

	@BeforeEach
	void setUp() {
		// DB is rolled back between tests due to @Transactional
		assertThat(mvc).isNotNull();
		assertThat(repo).isNotNull();
	}

	@Test
	void create_then_get_update_delete() throws Exception {
		// 1) CREATE
		OrderDto create = new OrderDto();
		create.setCode("IT-1001");
		create.setStatus("NEW");

		String createdJson = mvc
				.perform(post("/orderflow-api/api/orders").contentType(MediaType.APPLICATION_JSON)
						.content(om.writeValueAsString(create)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.code").value("IT-1001")).andExpect(jsonPath("$.status").value("NEW")).andReturn()
				.getResponse().getContentAsString();

		OrderDto created = om.readValue(createdJson, OrderDto.class);
		Long id = created.getId();
		assertThat(id).isNotNull();

		// 2) GET BY ID
		mvc.perform(get("/orderflow-api/api/orders/{id}", id)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id)).andExpect(jsonPath("$.code").value("IT-1001"))
				.andExpect(jsonPath("$.status").value("NEW"));

		// 3) UPDATE
		OrderDto upd = new OrderDto();
		upd.setCode("IT-1001");
		upd.setStatus("PROCESSING");

		mvc.perform(put("/orderflow-api/api/orders/{id}", id).contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(upd))).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("PROCESSING"));

		// 4) LIST
		mvc.perform(get("/orderflow-api/api/orders")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").isNumber());

		// 5) DELETE
		mvc.perform(delete("/orderflow-api/api/orders/{id}", id)).andExpect(status().isNoContent());

		// 6) GET after delete -> 404 via GlobalExceptionHandler
		mvc.perform(get("/orderflow-api/api/orders/{id}", id)).andExpect(status().isNotFound());
	}

	@Test
	void validation_errors_are_422() throws Exception {
		OrderDto bad = new OrderDto();
		bad.setCode(""); // @NotBlank
		bad.setStatus(null); // @NotNull

		mvc.perform(post("/orderflow-api/api/orders").contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(bad))).andExpect(status().isUnprocessableEntity());
	}
}