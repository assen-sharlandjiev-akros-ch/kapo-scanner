package ch.akros.kapo;

import org.springframework.boot.ApplicationArguments;

public class ImageRoute extends AbstractRouteBuilder {

  ImageRoute(final ApplicationArguments arguments) {
    super(arguments);
  }

  @Override
  public void configure() throws Exception {
    final var outoutPath = getOutputPath("images");
    from("direct:imageRoute")
        .log("[IMAGE][${file:name}][ContentType: ${in.header['CamelFileContentType']}][Tika MediaType: ${in.header['CamelFileMediaType']}]")
        .to("file:".concat(outoutPath));
  }

}
