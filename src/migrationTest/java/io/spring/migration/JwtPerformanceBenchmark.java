package io.spring.migration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.spring.core.user.User;
import io.spring.infrastructure.service.DefaultJwtService;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(
    value = 1,
    jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class JwtPerformanceBenchmark {

  private DefaultJwtService jwtService;
  private User testUser;
  private String testToken;
  private SecretKey secretKey;

  @Setup
  public void setup() {
    jwtService = new DefaultJwtService("test-secret-key-for-benchmarking-purposes-only", 3600);
    testUser = new User("test@example.com", "testuser", "password", "bio", "image");
    testToken = jwtService.toToken(testUser);
    secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
  }

  @Benchmark
  public String benchmarkJwtTokenGeneration() {
    return jwtService.toToken(testUser);
  }

  @Benchmark
  public String benchmarkJwtTokenValidation() {
    return jwtService.getSubFromToken(testToken).orElse(null);
  }

  @Benchmark
  public String benchmarkDirectJjwtTokenGeneration() {
    return Jwts.builder()
        .setSubject(testUser.getId())
        .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000L))
        .signWith(secretKey)
        .compact();
  }

  @Benchmark
  public String benchmarkDirectJjwtTokenParsing() {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(testToken)
          .getBody()
          .getSubject();
    } catch (Exception e) {
      return null;
    }
  }

  public static void main(String[] args) throws RunnerException {
    Options opt =
        new OptionsBuilder().include(JwtPerformanceBenchmark.class.getSimpleName()).build();

    new Runner(opt).run();
  }
}
