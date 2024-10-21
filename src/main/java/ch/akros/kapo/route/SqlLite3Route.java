package ch.akros.kapo.route;

import static org.apache.camel.Exchange.FILE_NAME;

import org.apache.camel.Predicate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class SqlLite3Route extends AbstractRouteBuilder {

  SqlLite3Route(final ApplicationArguments arguments) {
    super(arguments);
  }

  @Override
  public void configure() throws Exception {
    final var outoutPath = getTargetPath("sqllite");
    final var toFile = "file:".concat(outoutPath);
    from("direct:sqlLiteRoute")
        .process(tikaProcessor())
        .process(fileMetadataProcessor())
        .log("[${file:name}][ContentType: ${in.header['CamelFileContentType']}][Tika MediaType: ${in.header['CamelFileMediaType']}]")
        .choice()
        .when(isIphoneSmsDB())
          .to(toFile)
          .to("direct:iphoneSmsDbRoute")
        .otherwise()
          .log("[SQLLite3][${file:name}][Tika MediaType: ${in.header['CamelFileMediaType']}][Unsuported SQLLite3 database][${in.header['TikaMetadata']}]")
          .to(toFile);
  }

  private Predicate isIphoneSmsDB() {
    return exchange -> "iphoneSms.db".equals(exchange.getIn().getHeader(FILE_NAME));
  }
}
