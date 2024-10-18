package ch.akros.kapo;

import org.apache.camel.Exchange;
import org.springframework.boot.ApplicationArguments;

public class ImageRoute extends AbstractRouteBuilder {

  ImageRoute(final ApplicationArguments arguments) {
    super(arguments);
  }

  @Override
  public void configure() throws Exception {
    final var outoutPath = getOutputPath("images");
    final var toFile = "file:".concat(outoutPath);
    from("direct:imageRoute")
        .to(toFile)
        .process(tikaProcessor())
        .process(fileMetadataProcessor())
        .log("[${file:name}][ContentType: ${in.header['CamelFileContentType']}][Tika MediaType: ${in.header['CamelFileMediaType']}]")
        .setBody(simple("${in.header[fileMetadata]}"))
        .setHeader(Exchange.FILE_NAME, simple("${in.header[fileMetadataJsonFileName]}"))
        .marshal().json()
        .to(toFile);
  }

}
