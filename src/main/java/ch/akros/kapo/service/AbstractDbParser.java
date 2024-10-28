package ch.akros.kapo.service;

import java.io.File;
import java.io.FileNotFoundException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.sqlite.JDBC;

public abstract class AbstractDbParser {

  protected final JdbcTemplate jdbcTemplate;

  public AbstractDbParser(final String dbFilePath) throws FileNotFoundException {
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

}
