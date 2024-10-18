package ch.akros.kapo;

import org.apache.camel.LoggingLevel;
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
        .when(simple("${header.CamelFileContentType} startsWith 'application/vnd.openxmlformats-officedocument'"))
          .to("direct:textRoute")
        .otherwise()
          .log(LoggingLevel.TRACE, "[${file:name}][ContentType: ${in.header['CamelFileContentType']}][Tika MediaType: ${in.header['CamelFileMediaType']}][Unsupported mime type]");
  }

}
