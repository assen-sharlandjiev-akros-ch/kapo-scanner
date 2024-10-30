package ch.akros.kapo.route;

import static org.apache.camel.Exchange.FILE_NAME;

import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class AudioRoute extends AbstractRouteBuilder {

  AudioRoute(final ApplicationArguments arguments) {
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

    from("direct:audioRoute")
        .setHeader("contentTypePath", constant("Audio"))
        .process(tikaProcessor())
        .process(fileMetadataProcessor())
        .log("[AUDIO][${file:name}][ContentType: ${in.header['CamelFileMediaType']}]")
        .setHeader(FILE_NAME, header("copyFileName"))
        .to(toFile)
        .setBody(header("fileMetadata"))
        .setHeader(FILE_NAME, header("fileMetadataJsonFileName"))
        .marshal().json()
        .to(toFile);
  }

}
