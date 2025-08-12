package io.spring.api;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "health")
public class HealthApi {

  @GetMapping
  public ResponseEntity<Map<String, Object>> getHealth() {
    Map<String, Object> healthStatus = new HashMap<>();
    healthStatus.put("status", "UP");
    healthStatus.put("timestamp", System.currentTimeMillis());
    healthStatus.put("service", "realworld-api");
    healthStatus.put("version", "1.0.0");
    
    return ResponseEntity.ok(healthStatus);
  }
}
