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
        .when(simple("${header.CamelFileContentType} startsWith 'image'"))
          .to("direct:imageRoute")
        .when(simple("${header.CamelFileContentType} startsWith 'video'"))
          .to("direct:videoRoute")
        .when(simple("${header.CamelFileContentType} startsWith 'text'"))
          .to("direct:textRoute")
        .when(simple("${header.CamelFileContentType} startsWith 'application/pdf'"))
          .to("direct:textRoute")
        .when(simple("${header.CamelFileContentType} startsWith 'application/vnd.openxmlformats-officedocument'"))
          .to("direct:textRoute")
        .when(simple("${header.CamelFileMediaType} startsWith 'application/x-sqlite3'"))
          .to("direct:sqlLiteRoute")
        .otherwise()
          .log(WARN, "[${file:name}][ContentType: ${in.header['CamelFileContentType']}][Tika MediaType: ${in.header['CamelFileMediaType']}][Unsupported mime type]");
  }

}
