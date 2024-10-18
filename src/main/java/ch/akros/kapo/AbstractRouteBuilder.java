package ch.akros.kapo;

import static java.util.Objects.nonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.boot.ApplicationArguments;

public abstract class AbstractRouteBuilder extends RouteBuilder {

  private final ApplicationArguments arguments;
  protected Tika tika;

  AbstractRouteBuilder(final ApplicationArguments arguments) {
    this.arguments = arguments;
    try {
      this.tika = new Tika();
    } catch (final Exception e) {
      log.error("Failed to instantiate Tika");
    }
  }

  protected List<String> getNonOptionArgs() {
    return arguments.getNonOptionArgs();

  }

  protected List<String> getOptionValues(final String name) {
    return arguments.getOptionValues(name);
  }

  protected String getOptionValue(final String name) {
    return arguments.getOptionValues(name).getFirst();
  }

  protected String getOutputPath(final String contentType) throws IOException {
    final var outoutPath = Path.of(getOptionValue("output"), contentType);
    if (!outoutPath.toFile().exists()) {
      Files.createDirectories(outoutPath);
    }
    return outoutPath.toString();
  }

  protected Processor tikaProcessor() {
    return e -> {
      final var file = ((File) e.getIn().getBody(GenericFile.class).getFile());
      final var parser = new AutoDetectParser();
      final var handler = new BodyContentHandler(Integer.MAX_VALUE);
      final var metadata = new Metadata();
      if (nonNull(tika)) {
        try (final var fos = new FileInputStream(file)) {
          parser.parse(fos, handler, metadata);
          final var text = handler.toString();
          e.getIn().setHeader("TikaText", text);
          e.getIn().setHeader("TikaMetadata", metadata);
        } catch (final Exception ex) {
          log.error("[TEXT][Fialed to extract text from: {}][Error: {}]", file.getAbsolutePath(), ex.getMessage());
        }
      }
    };
  }
}
