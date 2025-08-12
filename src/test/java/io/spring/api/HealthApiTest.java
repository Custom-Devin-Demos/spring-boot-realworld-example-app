package io.spring.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.spring.JacksonCustomizations;
import io.spring.core.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HealthApi.class)
@Import(JacksonCustomizations.class)
public class HealthApiTest {

  @Autowired private MockMvc mvc;
  
  @MockBean private UserRepository userRepository;

  @BeforeEach
  public void setUp() {
    RestAssuredMockMvc.mockMvc(mvc);
  }

  @Test
  public void should_get_health_status() {
    given()
        .when()
        .get("/health")
        .then()
        .statusCode(200)
        .body("status", equalTo("UP"))
        .body("service", equalTo("realworld-api"))
        .body("version", equalTo("1.0.0"))
        .body("timestamp", notNullValue());
  }
}
