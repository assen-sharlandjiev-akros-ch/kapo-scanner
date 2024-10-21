package ch.akros.kapo.route;

import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class VideoRoute extends AbstractRouteBuilder {

  VideoRoute(final ApplicationArguments arguments) {
    super(arguments);
  }

  @Override
  public void configure() throws Exception {
    final var outoutPath = getTargetPath("video");
    from("direct:videoRoute")
        .log("[VIDEO][${file:name}][ContentType: ${in.header['CamelFileContentType']}][Tika MediaType: ${in.header['CamelFileMediaType']}]")
        .to("file:".concat(outoutPath));
  }

}
