package ch.akros.kapo.service.iphone_sms;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class IphoneMessagesDbParserTest {

  @Test
  void Given_iphoneSmsDb_When_extractsData_Then_dataIsPresent() throws FileNotFoundException {
    final String dbFilePath = "./src/test/sqlite/iphoneSms.db";
    final var parser = new IphoneMessagesDbParser(dbFilePath);
    final var myNumber = parser.findMyNumber();
    final var contacts = parser.findAllContacts();
    var iphoneChatList = parser.parse();

    assertTrue(contacts.size() > 2);
    assertTrue(myNumber.isPresent());
    assertFalse(iphoneChatList.isEmpty());

    iphoneChatList.forEach(chat -> {
      assertFalse(chat.getContent().isBlank());
      assertFalse(chat.getFrom().isBlank());
      assertFalse(chat.getTo().isBlank());
      log.debug("From_{}_to{}: {}", chat.getFrom(), chat.getTo(), chat.getContent());

    });
  }

}
