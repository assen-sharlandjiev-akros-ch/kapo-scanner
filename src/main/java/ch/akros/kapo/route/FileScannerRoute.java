package ch.akros.kapo.route;

import java.io.File;

import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFile;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class FileScannerRoute extends AbstractRouteBuilder {


  FileScannerRoute(final ApplicationArguments arguments) {
    super(arguments);
  }

  @Override
  public void configure() throws Exception {
    final var sourcePath = getOptionValue("source", "/source");
    final var fromURI = String.format("file://%s?noop=true&recursive=true&maxMessagesPerPoll=1000&idempotentRepository=#memoryIdempotentRepository&delay=100", sourcePath);
    //final var fromURI = String.format("file://%s?recursive=true&maxMessagesPerPoll=1000&delay=100", sourcePath);
    onException(Exception.class)
        .onExceptionOccurred(onExceptionProcessor())
        .continued(true)
        .maximumRedeliveries(0);

    from(fromURI)
        .threads(1, 2)
        .process(contentTypeProcessor())
        .to("direct:contentTypeRoute");
  }

  private Processor contentTypeProcessor() {
    return e -> {
      final var file = ((File) e.getIn().getBody(GenericFile.class).getFile());
      final var mediaType = tika.detect(file);
      e.getIn().setHeader("CamelFileMediaType", mediaType);
    };
  }
}
