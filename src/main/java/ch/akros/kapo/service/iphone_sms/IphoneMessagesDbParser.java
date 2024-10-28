package ch.akros.kapo.service.iphone_sms;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.akros.kapo.service.AbstractDbParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IphoneMessagesDbParser extends AbstractDbParser {

  public IphoneMessagesDbParser(String dbFilePath) throws FileNotFoundException {
    super(dbFilePath);
  }

  public List<IphoneChat> parse() {
    final var contacts = findAllContacts();
    final var myNumber = findMyNumber()
        .orElseThrow(() -> new RuntimeException("My phone number not found"));

    final var iphoneChatList = new ArrayList<IphoneChat>();
    final var stingBuilder = new StringBuilder();
    for (int i = 0; i < contacts.size(); i++) {
      final String phoneNumber = contacts.get(i);
      final var messagesList = findMessagesBetweenTwoNumbers(myNumber, phoneNumber);

      for (final IphoneMessage message : messagesList) {
        final String sender = message.isFromMe() ? "Me(" + myNumber + ")" : phoneNumber;
        final String receiver = message.isFromMe() ? phoneNumber : "Me(" + myNumber + ")";
        stingBuilder.append("[" + message.getMessageType() + "]" + "[" + message.getMessageSentDate() + "] " + sender + " to " + receiver + ": " + message
            .getMessageContent() + "\n");
      }

      final var newChat = new IphoneChat(myNumber, phoneNumber, stingBuilder.toString());
      iphoneChatList.add(newChat);
      stingBuilder.setLength(0);
    }

    return iphoneChatList;
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

  public List<IphoneMessage> findMessagesBetweenTwoNumbers(final String number1, final String number2) {
    final var sql = """
        SELECT m.text, m.service, m.date, m.is_from_me FROM message m
        JOIN handle h ON h.rowid = m.handle_id
        WHERE  (h.id IN (?, ?) AND m.is_from_me = 1) OR (h.id IN (?, ?) AND m.is_from_me = 0)
        ORDER BY DATETIME((m.date / 1000000000) + 978307200, 'unixepoch') ASC; """;
    return jdbcTemplate.query(sql, ps -> {
      ps.setObject(1, number1);
      ps.setObject(2, number2);
      ps.setObject(3, number2);
      ps.setObject(4, number1);
    }, new IphoneMessageRowMapper());
  }

  public List<String> findAllContacts() {
    return jdbcTemplate.queryForList("""
          SELECT DISTINCT h.id FROM Handle h
        """, String.class);
  }
}
