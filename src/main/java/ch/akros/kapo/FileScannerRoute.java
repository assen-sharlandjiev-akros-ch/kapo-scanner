package ch.akros.kapo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.apache.tika.Tika;
import org.springframework.boot.ApplicationArguments;
import org.springframework.util.CollectionUtils;

public class FileScannerRoute extends RouteBuilder {

  private final ApplicationArguments arguments;
  private final AtomicBoolean scanComplete;
  private Tika tika;

  public FileScannerRoute(final ApplicationArguments arguments, final AtomicBoolean scanComplete) {
    this.arguments = arguments;
    this.scanComplete = scanComplete;
    try {
      this.tika = new Tika();
    } catch (final Exception e) {
      log.error("Failed to instantiate Tika");
    }
  }

  @Override
  public void configure() throws Exception {
    List<String> nonOptionArgs = arguments.getNonOptionArgs();
    final var path =  CollectionUtils.isEmpty(nonOptionArgs) ? "." : nonOptionArgs.get(0);
    final var fromURI = String.format("file://%s?noop=true&recursive=true&sendEmptyMessageWhenIdle=true&idempotentRepository=#repo", path);
    from(fromURI)
        .process(e -> scanComplete.set(Objects.isNull(e.getIn().getBody())))
        .filter(e -> Objects.nonNull(e.getIn().getBody()))
        .process(contentTypeProcessor())
        .log("[${file:name}][ContentType: ${in.header['CamelFileContentType']}][Tika MediaType: ${in.header['CamelFileMediaType']}]");
  }

  private Processor contentTypeProcessor() {
    return e -> {
      final var file = ((File) e.getIn().getBody(GenericFile.class).getFile());
      final var contentType = Files.probeContentType(file.toPath());
      e.getIn().setHeader("CamelFileContentType", contentType);
      if (Objects.nonNull(tika)) {
        try {
          final var mediaType = tika.detect(file);
          e.getIn().setHeader("CamelFileMediaType", mediaType);
        } catch (IOException e1) {
          log.error("Fialed to detect tika media type. Error: {}", e.getMessage());
        }
      }
    };
  }

}
