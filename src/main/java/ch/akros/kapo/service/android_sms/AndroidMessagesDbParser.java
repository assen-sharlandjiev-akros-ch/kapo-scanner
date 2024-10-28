package ch.akros.kapo.service.android_sms;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import ch.akros.kapo.service.AbstractDbParser;

public class AndroidMessagesDbParser extends AbstractDbParser {

  public AndroidMessagesDbParser(String dbFilePath) throws FileNotFoundException {
    super(dbFilePath);
  }

  public List<AndroidChat> parse() {
    final var contacts = findAllContacts();

    final var androidChatList = new ArrayList<AndroidChat>();
    final var stingBuilder = new StringBuilder();
    for (int i = 0; i < contacts.size(); i++) {
      final String phoneNumber = contacts.get(i);
      final var messagesList = findConversationWithNumber(phoneNumber);

      if (messagesList.isEmpty()) {
        continue;
      }

      for (final AndroidMessage message : messagesList) {
        stingBuilder.append("[" + message.getMessageDirection() + "]" + "[" + message.getMessageContent() + "] "
            + message.getMessageContent() + "[" + message.getMessageDate() + "]" + "\n");
      }

      final var newChat = new AndroidChat(phoneNumber, stingBuilder.toString());
      androidChatList.add(newChat);
      stingBuilder.setLength(0);
    }

    return androidChatList;
  }

  public List<AndroidMessage> findConversationWithNumber(String phoneNumber) {
    var sql = """
        SELECT
            CASE
                 WHEN type = 1 THEN 'From ' || address
                 WHEN type = 2 THEN 'To '   || address
            END AS message_direction,
            body AS message_content,
            date as timestamp
        FROM sms
        WHERE address = ?
        OR (type = 1 AND address = ?)
        OR (type = 2 AND address = ?)
        ORDER BY date ASC;
        """;

    return jdbcTemplate.query(sql, ps -> {
      ps.setObject(1, phoneNumber);
      ps.setObject(2, phoneNumber);
      ps.setObject(3, phoneNumber);
    }, new AndroidMessageRowMapper());

  }

  public List<String> findAllContacts() {
    return jdbcTemplate.queryForList("""
        SELECT address from canonical_addresses ca
             """, String.class);
  }

}
