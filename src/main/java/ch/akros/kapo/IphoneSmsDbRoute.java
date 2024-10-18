package ch.akros.kapo;

import org.springframework.boot.ApplicationArguments;

public class IphoneSmsDbRoute extends AbstractRouteBuilder {

  IphoneSmsDbRoute(final ApplicationArguments arguments) {
    super(arguments);
  }

  @Override
  public void configure() throws Exception {
    from("direct:iphoneSmsDbRoute")
        .to("mock:result");
  }

}
