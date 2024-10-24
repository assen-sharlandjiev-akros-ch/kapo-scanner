package ch.akros.kapo.route;

import static org.apache.camel.Exchange.FILE_NAME;

import org.apache.camel.Predicate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class Sqlite3Route extends AbstractRouteBuilder {

  Sqlite3Route(final ApplicationArguments arguments) {
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

    from("direct:sqliteRoute")
        .process(tikaProcessor())
        .process(fileMetadataProcessor())
        .log("[${file:name}][ContentType: ${in.header['CamelFileMediaType']}]")
        .choice()
        .when(isIphoneSmsDB())
        .to(toFile)
        .to("direct:iphoneSmsDbRoute")
        .otherwise()
        //.log("[SQLLite3][${file:name}][Tika MediaType: ${in.header['CamelFileMediaType']}][Unsuported SQLLite3 database][${in.header['TikaMetadata']}]")
        .to(toFile)
        .to("direct:unknownSqliteDbRoute");
  }

  private Predicate isIphoneSmsDB() {
    return exchange -> "sms.db".equals(exchange.getIn().getHeader(FILE_NAME));
  }
}
