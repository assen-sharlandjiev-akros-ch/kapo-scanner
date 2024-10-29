package ch.akros.kapo.route;

import org.apache.camel.Exchange;
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

    onException(Exception.class)
        .onExceptionOccurred(onExceptionProcessor())
        .continued(true)
        .maximumRedeliveries(0);

    from("direct:sqliteRoute")
        .choice()
        .when(isIphoneSmsDB())
        .to("direct:iphoneSmsDbRoute")
        .when(isAndroidSmsDB())
        .to("direct:androidSmsDbRoute")
        .otherwise()
        .to("direct:unknownSqliteDbRoute");
  }

  private Predicate isIphoneSmsDB() {
    return exchange -> {
      final var fileName = exchange.getIn().getHeader(Exchange.FILE_NAME_ONLY, String.class);
      return "sms.db".equalsIgnoreCase(fileName);
    };
  }

  private Predicate isAndroidSmsDB() {
    return exchange -> {
      final var fileName = exchange.getIn().getHeader(Exchange.FILE_NAME_ONLY, String.class);
      return "mmssms.db".equalsIgnoreCase(fileName);
    };
  }
}
