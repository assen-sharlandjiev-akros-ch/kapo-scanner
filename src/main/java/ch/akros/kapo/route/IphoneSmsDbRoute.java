package ch.akros.kapo.route;

import static org.apache.camel.Exchange.FILE_NAME;

import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class IphoneSmsDbRoute extends AbstractRouteBuilder {

  IphoneSmsDbRoute(final ApplicationArguments arguments) {
    super(arguments);
  }

  @Override
  public void configure() throws Exception {
    final var outoutPath = getTargetPath("sqllite");
    final var toFile = "file:".concat(outoutPath);
    from("direct:iphoneSmsDbRoute")
        .setBody(header("fileMetadata"))
        .setHeader(FILE_NAME, header("fileMetadataJsonFileName"))
        .marshal().json()
        .to(toFile)
        .setBody(header("TikaText"))
        .setHeader(FILE_NAME, header("tikaTextFileName"))
        .to(toFile);
  }

}
