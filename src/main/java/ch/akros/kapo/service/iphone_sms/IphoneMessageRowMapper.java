package ch.akros.kapo.service.iphone_sms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.springframework.jdbc.core.RowMapper;

public class IphoneMessageRowMapper implements RowMapper<IphoneMessage> {

  @Override
  public IphoneMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
    final var messageContent = rs.getString("text");
    // SMS or iMessage
    final var messageType = rs.getString("service");
    final var appleCocoaCoreDataTimestampInNanoSeconds = rs.getLong("date");
    final var isMessageFromMe = rs.getBoolean("is_from_me");
    final var messageSentDate = getMessageSentDateFormatted(appleCocoaCoreDataTimestampInNanoSeconds);

    return new IphoneMessage(messageType, messageContent, isMessageFromMe, messageSentDate);
  }

  private String getMessageSentDateFormatted(long appleCocoaCoreDataTimestampInNanoSeconds) {
    var adjustedValueInNanoSecondsForEpochTime = 978307200000000000L;
    var adjustedTimestamp = appleCocoaCoreDataTimestampInNanoSeconds + adjustedValueInNanoSecondsForEpochTime;
    long dateEpochSeconds = TimeUnit.SECONDS.convert(adjustedTimestamp, TimeUnit.NANOSECONDS);

    var instantEpochSeconds = Instant.ofEpochSecond(dateEpochSeconds);
    var dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC);

    return dateTimeFormatter.format(instantEpochSeconds);
  }
}
