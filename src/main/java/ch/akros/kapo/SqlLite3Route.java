package ch.akros.kapo;

import static org.apache.camel.Exchange.FILE_NAME;

import org.apache.camel.Predicate;
import org.springframework.boot.ApplicationArguments;

public class SqlLite3Route extends AbstractRouteBuilder {

  SqlLite3Route(final ApplicationArguments arguments) {
    super(arguments);
  }

  @Override
  public void configure() throws Exception {
    final var outoutPath = getOutputPath("video");
    from("direct:sqlLiteRoute")
        .process(tikaProcessor())
        .choice()
        .when(isIphoneSmsDB())
          .log("[SQLLite3][${file:name}][Tika MediaType: ${in.header['CamelFileMediaType']}][${in.header['TikaMetadata']}]")
          .log("${in.header['TikaText']}")
          .to("direct:iphoneSmsDbRoute")
        .otherwise()
          .log("[SQLLite3][${file:name}][Tika MediaType: ${in.header['CamelFileMediaType']}][Unsuported SQLLite3 database][${in.header['TikaMetadata']}]")
        .to("file:".concat(outoutPath));
  }

  private Predicate isIphoneSmsDB() {
    return exchange -> "iphoneSms.db".equals(exchange.getIn().getHeader(FILE_NAME));
  }
}
