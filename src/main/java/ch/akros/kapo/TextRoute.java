package ch.akros.kapo;

import static java.util.Objects.nonNull;

import java.io.File;
import java.io.FileInputStream;

import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFile;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.boot.ApplicationArguments;

public class TextRoute extends AbstractRouteBuilder {

  private Tika tika;

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
    from("direct:textRoute")
        .to("file:".concat(outoutPath))
        .process(textProcessor())
        .log("[TEXT][${file:name}][ContentType: ${in.header['CamelFileContentType']}][Tika MediaType: ${in.header['CamelFileMediaType']}][${in.header['TikaMetadata']}]");
  }

  private Processor textProcessor() {
    return e -> {
      final var file = ((File) e.getIn().getBody(GenericFile.class).getFile());
      final var parser = new AutoDetectParser();
      final var handler = new BodyContentHandler();
      final var metadata = new Metadata();
      if (nonNull(tika)) {
        try (final var fos = new FileInputStream(file)) {
          parser.parse(fos, handler, metadata);
          final var text = handler.toString();
          e.getIn().setHeader("TikaText", text);
          e.getIn().setHeader("TikaMetadata", metadata);
        } catch (final Exception ex) {
          log.error("[TEXT][Fialed to extract text from: {}]", file.getAbsolutePath());
        }
      }
    };
  }
}
