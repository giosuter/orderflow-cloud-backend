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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test Full-stack test: controller + service + repo + DB (H2 for
 * the 'test' profile).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
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
		// 1) CREATE (valid payload)
		OrderDto create = new OrderDto();
		create.setCode("IT-1001");
		create.setStatus("NEW"); // must match your enum
		create.setTotal(new BigDecimal("10.00")); // satisfy @NotNull @Positive if present

		String createdJson = mvc
				.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
						.content(om.writeValueAsString(create)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.code").value("IT-1001")).andExpect(jsonPath("$.status").value("NEW"))
				.andExpect(jsonPath("$.total").value(10.00)).andReturn().getResponse().getContentAsString();

		OrderDto created = om.readValue(createdJson, OrderDto.class);
		Long id = created.getId();
		assertThat(id).isNotNull();

		// 2) GET BY ID
		mvc.perform(get("/api/orders/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.code").value("IT-1001")).andExpect(jsonPath("$.status").value("NEW"))
				.andExpect(jsonPath("$.total").value(10.00));

		// 3) UPDATE (keep valid enum status; change total)
		OrderDto upd = new OrderDto();
		upd.setCode("IT-1001");
		upd.setStatus("NEW"); // keep same valid enum to avoid 400
		upd.setTotal(new BigDecimal("15.25"));

		mvc.perform(
				put("/api/orders/{id}", id).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(upd)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.status").value("NEW"))
				.andExpect(jsonPath("$.total").value(15.25));

		// 4) LIST
		mvc.perform(get("/api/orders")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").isNumber());

		// 5) DELETE
		mvc.perform(delete("/api/orders/{id}", id)).andExpect(status().isNoContent());

		// 6) GET after delete -> 404 via GlobalExceptionHandler
		mvc.perform(get("/api/orders/{id}", id)).andExpect(status().isNotFound());
	}

	@Test
	void validation_errors_are_422() throws Exception {
		// Missing/invalid fields to trigger bean validation errors
		OrderDto bad = new OrderDto();
		bad.setCode(""); // @NotBlank
		bad.setStatus(null); // @NotNull (and/or enum)
		// total omitted => @NotNull/@Positive should fail as seen in logs

		mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(bad)))
				.andExpect(status().isUnprocessableEntity());
	}
}