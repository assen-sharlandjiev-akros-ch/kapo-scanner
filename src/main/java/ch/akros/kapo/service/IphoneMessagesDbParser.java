package ch.akros.kapo.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.sqlite.JDBC;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IphoneMessagesDbParser {

  @Data
  public static class Chat {
    private String from;
    private String to;
    private String content;

    public String fileName() {
      return String.format("%s-%s.txt", from, to);
    }
  }

  private final JdbcTemplate jdbcTemplate;

  public IphoneMessagesDbParser(final String dbFilePath) throws FileNotFoundException {
    final var file = new File(dbFilePath);
    if (!file.exists()) {
      throw new FileNotFoundException();
    }
    final var dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(JDBC.class.getName());
    dataSource.setUrl("jdbc:sqlite:".concat(dbFilePath));
    jdbcTemplate = new JdbcTemplate(dataSource);
    jdbcTemplate.afterPropertiesSet();
  }

  public List<Chat> parse() throws FileNotFoundException {
    final var myNumber = findMyNumber();
    log.info("My Number: {}", myNumber);
    return Collections.emptyList();
  }

  public Optional<String> findMyNumber() {
    return Optional.of(jdbcTemplate.queryForObject("""
      SELECT handle.id AS my_number
      FROM message
      JOIN handle ON message.handle_id = handle.ROWID
      WHERE message.is_from_me = 1
      LIMIT 1
      """, String.class));
  }

}
