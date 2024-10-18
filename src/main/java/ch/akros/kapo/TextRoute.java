package ch.akros.kapo;

import static org.apache.camel.Exchange.FILE_NAME;

import org.apache.tika.Tika;
import org.springframework.boot.ApplicationArguments;

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
    final var outoutPath = getOutputPath("text");
    final var toFile = "file:".concat(outoutPath);
    from("direct:textRoute")
        .to(toFile)
        .process(tikaProcessor())
        .process(fileMetadataProcessor())
        .log("[${file:name}][ContentType: ${in.header['CamelFileContentType']}][Tika MediaType: ${in.header['CamelFileMediaType']}]")
        .setBody(header("fileMetadata"))
        .setHeader(FILE_NAME, header("fileMetadataJsonFileName"))
        .marshal().json()
        .to(toFile)
        .setBody(header("TikaText"))
        .setHeader(FILE_NAME, header("tikaTextFileName"))
        .to(toFile);
  }
}
