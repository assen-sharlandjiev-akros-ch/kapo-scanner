package ch.akros.kapo.route;

import static org.apache.camel.Exchange.FILE_NAME;

import org.apache.camel.Exchange;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class ImageRoute extends AbstractRouteBuilder {

  ImageRoute(final ApplicationArguments arguments) {
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

    from("direct:imageRoute")
        .setHeader("contentTypePath", constant("Images"))
        .process(tikaProcessor())
        .process(fileMetadataProcessor())
        .log("[${file:name}][ContentType: ${in.header['CamelFileMediaType']}]")
        // file
        .setHeader(FILE_NAME, header("copyFileName"))
        .to(toFile)
        // metadata
        .setBody(simple("${in.header[fileMetadata]}"))
        .setHeader(Exchange.FILE_NAME, simple("${in.header[fileMetadataJsonFileName]}"))
        .marshal().json()
        .to(toFile);
  }

}
