package io.spring.migration;

import static org.junit.jupiter.api.Assertions.*;

import com.netflix.graphql.dgs.DgsComponent;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;
import javax.crypto.SecretKey;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class DependencyCompatibilityTest {

  @Autowired(required = false)
  private SqlSessionFactory sqlSessionFactory;

  @Test
  public void testMyBatisCompatibility() {
    assertNotNull(sqlSessionFactory, "MyBatis SqlSessionFactory should be available");

    assertDoesNotThrow(
        () -> {
          sqlSessionFactory.openSession();
        },
        "MyBatis should be able to open sessions without errors");
  }

  @Test
  public void testNetflixDgsCompatibility() {
    assertDoesNotThrow(
        () -> {
          Class.forName("com.netflix.graphql.dgs.DgsComponent");
        },
        "Netflix DGS framework classes should be available");

    assertNotNull(DgsComponent.class, "DgsComponent annotation class should be available");
  }

  @Test
  public void testJJWTCompatibility() {
    assertDoesNotThrow(
        () -> {
          SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

          String jwt =
              Jwts.builder()
                  .setSubject("test-user")
                  .setExpiration(new Date(System.currentTimeMillis() + 60000))
                  .signWith(key)
                  .compact();

          assertNotNull(jwt);
          assertTrue(jwt.split("\\.").length == 3);

          String subject =
              Jwts.parserBuilder()
                  .setSigningKey(key)
                  .build()
                  .parseClaimsJws(jwt)
                  .getBody()
                  .getSubject();

          assertEquals("test-user", subject);
        },
        "JJWT should work correctly for JWT operations");
  }

  @Test
  public void testSQLiteJDBCCompatibility() {
    assertDoesNotThrow(
        () -> {
          Class.forName("org.sqlite.JDBC");
        },
        "SQLite JDBC driver should be available");

    assertDoesNotThrow(
        () -> {
          try (Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            assertNotNull(conn);
            assertFalse(conn.isClosed());

            conn.createStatement().execute("CREATE TABLE test (id INTEGER PRIMARY KEY)");
            conn.createStatement().execute("INSERT INTO test (id) VALUES (1)");

            var rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM test");
            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
          }
        },
        "SQLite JDBC should support basic database operations");
  }

  @Test
  public void testSpringBootCompatibility() {
    assertDoesNotThrow(
        () -> {
          Class.forName("org.springframework.boot.SpringApplication");
          Class.forName("org.springframework.boot.autoconfigure.SpringBootApplication");
        },
        "Spring Boot core classes should be available");
  }

  @Test
  public void testSpringSecurityCompatibility() {
    assertDoesNotThrow(
        () -> {
          Class.forName(
              "org.springframework.security.config.annotation.web.configuration.EnableWebSecurity");
          Class.forName("org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder");
        },
        "Spring Security classes should be available");
  }

  @Test
  public void testValidationCompatibility() {
    assertDoesNotThrow(
        () -> {
          Class.forName("javax.validation.Valid");
          Class.forName("javax.validation.constraints.NotBlank");
          Class.forName("javax.validation.constraints.Email");
        },
        "Validation annotations should be available");
  }

  @Test
  public void testJacksonCompatibility() {
    assertDoesNotThrow(
        () -> {
          Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
          Class.forName("com.fasterxml.jackson.databind.module.SimpleModule");
        },
        "Jackson classes should be available for JSON processing");
  }
}
