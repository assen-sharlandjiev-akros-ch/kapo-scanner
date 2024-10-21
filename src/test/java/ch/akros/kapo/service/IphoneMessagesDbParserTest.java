package ch.akros.kapo.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class IphoneMessagesDbParserTest {

  @Test
  void test() throws FileNotFoundException {
    final String dbFilePath = "./src/test/sqlite/iphoneSms.db";
    final var parser = new IphoneMessagesDbParser(dbFilePath);
    final var myNumber = parser.findMyNumber();

    assertTrue(myNumber.isPresent());
    log.info("My number: {}", myNumber.get());

  }

}
