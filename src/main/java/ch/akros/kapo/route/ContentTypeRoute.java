package ch.akros.kapo.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import ch.akros.kapo.KapoConfigurationProperties;

@Component
public class ContentTypeRoute extends AbstractRouteBuilder {

  private final KapoConfigurationProperties props;

  ContentTypeRoute(final ApplicationArguments arguments, final KapoConfigurationProperties props) {
    super(arguments);
    this.props = props;
  }

  @Override
  public void configure() throws Exception {
    from("direct:contentTypeRoute")
        .choice()
        .when(simple("${header.CamelFileMediaType} startsWith 'image'"))
        .to("direct:imageRoute")
        .when(simple("${header.CamelFileMediaType} startsWith 'audio'"))
        .to("direct:audioRoute")
        .when(simple("${header.CamelFileMediaType} startsWith 'video'"))
        .to("direct:videoRoute")
        .when(isTextDocument())
        .to("direct:textRoute")
        .when(simple("${header.CamelFileMediaType} startsWith 'application/x-sqlite3'"))
        .to("direct:sqliteRoute")
        .otherwise()
        .log(LoggingLevel.TRACE, "[${file:name}][ContentType: ${in.header['CamelFileMediaType']}][Unsupported mime type]");
  }

  private final Predicate isTextDocument() {
    return exchange -> {
      final var contentType = exchange.getIn().getHeader("CamelFileMediaType", String.class);
      return props.getDocumentContentTypes().stream().anyMatch(contentTypePrefix -> contentType.startsWith(contentTypePrefix));
    };
  }

}
