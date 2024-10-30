package ch.akros.kapo.route;

import static org.apache.camel.LoggingLevel.WARN;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ContentTypeRoute extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    from("direct:contentTypeRoute")
      .choice()
        .when(simple("${header.CamelFileMediaType} startsWith 'image'"))
          .to("direct:imageRoute")
       . when(simple("${header.CamelFileMediaType} startsWith 'audio'"))
          .to("direct:audioRoute")
        .when(simple("${header.CamelFileMediaType} startsWith 'video'"))
          .to("direct:videoRoute")
        .when(simple("${header.CamelFileMediaType} startsWith 'text'"))
          .to("direct:textRoute")
        .when(simple("${header.CamelFileMediaType} startsWith 'application/pdf'"))
          .to("direct:textRoute")
        .when(simple("${header.CamelFileMediaType} startsWith 'application/vnd.openxmlformats-officedocument'"))
          .to("direct:textRoute")
        .when(simple("${header.CamelFileMediaType} startsWith 'application/x-sqlite3'"))
          .to("direct:sqliteRoute")
        .otherwise()
          .log(WARN, "[${file:name}][ContentType: ${in.header['CamelFileMediaType']}][Unsupported mime type]");
  }

}
