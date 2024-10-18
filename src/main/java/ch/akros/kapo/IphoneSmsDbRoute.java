package ch.akros.kapo;

import static org.apache.camel.Exchange.FILE_NAME;

import org.springframework.boot.ApplicationArguments;

public class IphoneSmsDbRoute extends AbstractRouteBuilder {

  IphoneSmsDbRoute(final ApplicationArguments arguments) {
    super(arguments);
  }

  @Override
  public void configure() throws Exception {
    final var outoutPath = getOutputPath("sqllite");
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
