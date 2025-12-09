package ch.devprojects.orderflow.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple debug endpoint to test how the application behaves when an Origin
 * header is present.
 *
 * This controller does NOT configure CORS itself. It relies on the global CORS
 * configuration in WebCorsConfig (applied to /api/**).
 */
@RestController
@RequestMapping("/api/debug")
public class DebugOriginController {

	@GetMapping("/origin")
	public ResponseEntity<String> checkOrigin(@RequestHeader(name = "Origin", required = false) String origin) {

		String msg = "Debug OK. Origin header = " + (origin == null ? "<none>" : origin);
		return ResponseEntity.ok(msg);
	}
}