package ch.akros.kapo.route;

import static org.apache.camel.Exchange.FILE_NAME;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import ch.akros.kapo.service.iphone_sms.IphoneMessagesDbParser;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IphoneSmsDbRoute extends AbstractRouteBuilder {

  IphoneSmsDbRoute(final ApplicationArguments arguments) {
    super(arguments);
  }

  @Override
  public void configure() throws Exception {
    final var outoutPath = getTargetPath();
    final var toFile = "file:".concat(outoutPath);

    onException(Exception.class)
        .onExceptionOccurred(onExceptionProcessor())
        .continued(true)
        .maximumRedeliveries(0);

    from("direct:iphoneSmsDbRoute")
        .setHeader("contentTypePath", constant("Communications/SMS"))
        .process(tikaProcessor())
        .process(fileMetadataProcessor())
        .log("[${file:name}][ContentType: ${in.header['CamelFileMediaType']}]")
        .setHeader(FILE_NAME, header("copyFileName"))
        .to(toFile)
        .setBody(header("fileMetadata"))
        .setHeader(FILE_NAME, header("fileMetadataJsonFileName"))
        .marshal().json()
        .to(toFile)
        .setBody(header("TikaText"))
        .setHeader(FILE_NAME, header("tikaTextFileName"))
        .to(toFile)
        .process(iphoneMessageDbProcessor())
        .split(body())
        .setHeader(FILE_NAME, simple("${in.header['filePathPrefix']}-${body.fileName()}"))
        .setBody(simple("${body.content}"))
        .to(toFile);
  }

  private Processor iphoneMessageDbProcessor() {
    return e -> {
      final var dbFilePath = e.getIn().getHeader(Exchange.FILE_PATH, String.class);
      try {
        final var parser = new IphoneMessagesDbParser(dbFilePath);
        final var chats = parser.parse();
        e.getIn().setBody(chats);
      } catch (final Exception ex) {
        log.error(ex.getMessage(), ex);
      }
    };
  }

}
