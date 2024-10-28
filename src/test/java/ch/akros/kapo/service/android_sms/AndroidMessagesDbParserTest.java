package ch.akros.kapo.service.android_sms;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class AndroidMessagesDbParserTest {

  @Test
  void Given_androidSmsDb_When_extractsData_Then_dataIsPresent() throws FileNotFoundException {
    final String dbFilePath = "./src/test/sqlite/mmssms.db";
    final var parser = new AndroidMessagesDbParser(dbFilePath);
    final var contacts = parser.findAllContacts();
    final var androidChatList = parser.parse();

    assertTrue(contacts.size() > 1);
    assertFalse(androidChatList.isEmpty());

    androidChatList.forEach(chat -> {
      assertFalse(chat.getContent().isBlank());
      assertFalse(chat.getNumber().isBlank());
      log.debug("Chat: {}", chat.getContent());
    });
  }

  @Test
  void Given_missingSmsDb_When_extractsData_Then_exceptionIsThrown() {
    assertThrows(FileNotFoundException.class, () -> {
      final var parser = new AndroidMessagesDbParser("");
    });
  }
}
