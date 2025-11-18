package ch.devprojects.orderflow.web;

import java.time.Instant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PingController {
	
	public static final String PING_MESSAGE = "OrderFlow API is alive - v2.2";


	@GetMapping("/ping")
	public ResponseEntity<String> ping() {
		return ResponseEntity.ok(PING_MESSAGE);
	}

	@GetMapping("/ping/pong")
	public ResponseEntity<String> pong() {
		return ResponseEntity.ok("pong");
	}

	@GetMapping("/ping/time")
	public String pingTime() {
		return "ping_the_endpoint_time@" + Instant.now();
	}
}