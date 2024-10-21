package ch.akros.kapo.route;

import static java.util.Objects.nonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFile;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class FileScannerRoute extends AbstractRouteBuilder {

  private final AtomicBoolean scanComplete;

  FileScannerRoute(final ApplicationArguments arguments, final AtomicBoolean scanComplete) {
    super(arguments);
    this.scanComplete = scanComplete;
  }

  @Override
  public void configure() throws Exception {
    final var sourcePath = getOptionValue("source", "/source");
    final var fromURI = String.format("file://%s?noop=true&recursive=true&sendEmptyMessageWhenIdle=true&idempotentRepository=#repo", sourcePath);
    from(fromURI)
        .process(e -> scanComplete.set(Objects.isNull(e.getIn().getBody())))
        .filter(e -> Objects.nonNull(e.getIn().getBody()))
        .process(contentTypeProcessor())
        .to("direct:contentTypeRoute");
  }

  private Processor contentTypeProcessor() {
    return e -> {
      final var file = ((File) e.getIn().getBody(GenericFile.class).getFile());
      final var contentType = Files.probeContentType(file.toPath());
      e.getIn().setHeader("CamelFileContentType", contentType);
      if (nonNull(tika)) {
        try {
          final var mediaType = tika.detect(file);
          e.getIn().setHeader("CamelFileMediaType", mediaType);
        } catch (final IOException e1) {
          log.error("Fialed to detect tika media type. Error: {}", e.getMessage());
        }
      }
    };
  }

}
