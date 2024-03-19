package com.prototype.patterns;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;

@RestController
public class BulkheadPattern {
	
	// BulkheadFullException thrown will be handled by Exception Controller
	@GetMapping("/api/bulkhead")
	@Bulkhead(name="bulkheadApi")
	public ResponseEntity<String> bulkheadApi() {
		
		try {
			Thread.sleep(8000);
		} catch (InterruptedException ignore) {
			
		}
		
		return new ResponseEntity<>("success", HttpStatus.OK);
	}
}
