package ch.akros.kapo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.ApplicationArguments;

public abstract class AbstractRouteBuilder extends RouteBuilder {

  private final ApplicationArguments arguments;

  AbstractRouteBuilder(final ApplicationArguments arguments) {
    this.arguments = arguments;
  }

  protected List<String> getNonOptionArgs() {
    return arguments.getNonOptionArgs();

  }

  protected List<String> getOptionValues(final String name) {
    return arguments.getOptionValues(name);
  }

  protected String getOptionValue(final String name) {
    return arguments.getOptionValues(name).getFirst();
  }

  protected String getOutputPath(final String contentType) throws IOException {
    final var outoutPath = Path.of(getOptionValue("output"), contentType);
    if (!outoutPath.toFile().exists()) {
      Files.createDirectories(outoutPath);
    }
    return outoutPath.toString();
  }
}
