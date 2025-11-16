package ch.devprojects.orderflow.web;

import java.time.Instant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PingController {

	@GetMapping("/ping")
	public ResponseEntity<String> ping() {
		return ResponseEntity.ok("OrderFlow API is alive - Giovanni Suter copyright - running...");
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