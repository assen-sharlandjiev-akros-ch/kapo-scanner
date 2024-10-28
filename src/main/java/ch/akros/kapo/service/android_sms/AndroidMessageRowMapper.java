package ch.akros.kapo.service.android_sms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.springframework.jdbc.core.RowMapper;

public class AndroidMessageRowMapper implements RowMapper<AndroidMessage> {

  @Override
  public AndroidMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
    final var messageContent = rs.getString("message_content");
    final var messageDirection = rs.getString("message_direction");
    final var messageDate = getMessageDateFormatted(rs.getLong("timestamp"));

    return new AndroidMessage(messageDirection, messageContent, messageDate);
  }

  private String getMessageDateFormatted(final long timestamp) {
    final var instantEpochSeconds = Instant.ofEpochMilli(timestamp);
    final var dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC);

    return dateTimeFormatter.format(instantEpochSeconds);
  }

}
