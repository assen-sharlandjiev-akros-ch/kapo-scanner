package ch.akros.kapo.route;

import static org.apache.camel.Exchange.FILE_NAME;

import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UnknownSqliteDbRoute extends AbstractRouteBuilder {

  UnknownSqliteDbRoute(final ApplicationArguments arguments) {
    super(arguments);
  }

  @Override
  public void configure() throws Exception {
    final var outoutPath = getTargetPath("sqllite");
    final var toFile = "file:".concat(outoutPath);
    onException(Exception.class)
    .onExceptionOccurred(onExceptionProcessor())
    .continued(true)
    .maximumRedeliveries(0);
    from("direct:unknownSqliteDbRoute")
        .setBody(header("fileMetadata"))
        .setHeader(FILE_NAME, header("fileMetadataJsonFileName"))
        .marshal().json()
        .to(toFile)
        .setBody(header("TikaText"))
        .setHeader(FILE_NAME, header("tikaTextFileName"))
        .to(toFile);
  }

}
