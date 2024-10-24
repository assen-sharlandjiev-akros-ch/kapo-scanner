package ch.akros.kapo.route;

import static org.apache.camel.Exchange.FILE_NAME;

import org.apache.tika.Tika;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class TextRoute extends AbstractRouteBuilder {

  TextRoute(final ApplicationArguments arguments) {
    super(arguments);
    try {
      this.tika = new Tika();
    } catch (final Exception e) {
      log.error("Failed to instantiate Tika");
    }
  }

  @Override
  public void configure() throws Exception {
    final var outoutPath = getTargetPath("text");
    final var toFile = "file:".concat(outoutPath);

    onException(Exception.class)
        .onExceptionOccurred(onExceptionProcessor())
        .continued(true)
        .maximumRedeliveries(0);

    from("direct:textRoute")
        .to(toFile)
        .process(tikaProcessor())
        .process(fileMetadataProcessor())
        .log("[${file:name}][ContentType: ${in.header['CamelFileMediaType']}]")
        .setBody(header("fileMetadata"))
        .setHeader(FILE_NAME, header("fileMetadataJsonFileName"))
        .marshal().json()
        .to(toFile)
        .setBody(header("TikaText"))
        .setHeader(FILE_NAME, header("tikaTextFileName"))
        .to(toFile);
  }
}
