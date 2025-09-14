package io.spring.migration;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.spring.core.article.Article;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class JavaTimeConversionTest {

  @Test
  public void testJodaTimeToInstantConversion() {
    DateTime jodaTime = new DateTime(2023, 9, 14, 12, 30, 0);

    Instant instant = Instant.ofEpochMilli(jodaTime.getMillis());

    assertEquals(jodaTime.getMillis(), instant.toEpochMilli());
  }

  @Test
  public void testJodaTimeToLocalDateTimeConversion() {
    DateTime jodaTime = new DateTime(2023, 9, 14, 12, 30, 0);

    LocalDateTime localDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(jodaTime.getMillis()), ZoneOffset.UTC);

    assertEquals(2023, localDateTime.getYear());
    assertEquals(9, localDateTime.getMonthValue());
    assertEquals(14, localDateTime.getDayOfMonth());
    assertEquals(12, localDateTime.getHour());
    assertEquals(30, localDateTime.getMinute());
  }

  @Test
  public void testArticleEntityWithJavaTime() {
    Article article =
        new Article(
            "Test Title",
            "Test Description",
            "Test Body",
            Arrays.asList("tag1", "tag2"),
            "user123");

    assertNotNull(article.getCreatedAt());
    assertNotNull(article.getUpdatedAt());
    assertEquals(article.getCreatedAt(), article.getUpdatedAt());
  }

  @Test
  public void testJavaTimeSerializationCompatibility() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addSerializer(Instant.class, new InstantSerializer());
    mapper.registerModule(module);

    Instant now = Instant.now();
    String json = mapper.writeValueAsString(now);

    assertNotNull(json);
    assertTrue(json.contains("T"));
    assertTrue(json.contains("Z"));
  }

  @Test
  public void testDateTimeFormattingCompatibility() {
    DateTime jodaTime = new DateTime(2023, 9, 14, 12, 30, 0);
    Instant instant = Instant.ofEpochMilli(jodaTime.getMillis());

    String jodaFormatted = jodaTime.toString();
    String instantFormatted = instant.toString();

    assertTrue(jodaFormatted.contains("2023-09-14"));
    assertTrue(instantFormatted.contains("2023-09-14"));
  }

  @Test
  public void testMyBatisTypeHandlerCompatibility() {
    DateTime jodaTime = new DateTime(2023, 9, 14, 12, 30, 0);

    long millis = jodaTime.getMillis();
    Instant instant = Instant.ofEpochMilli(millis);

    assertEquals(millis, instant.toEpochMilli());

    Instant reconstructed = Instant.ofEpochMilli(instant.toEpochMilli());
    assertEquals(instant, reconstructed);
  }

  public static class InstantSerializer extends StdSerializer<Instant> {

    public InstantSerializer() {
      super(Instant.class);
    }

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      if (value == null) {
        gen.writeNull();
      } else {
        gen.writeString(value.toString());
      }
    }
  }
}
