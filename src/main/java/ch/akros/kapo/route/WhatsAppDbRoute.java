package ch.akros.kapo.route;

import static org.apache.camel.Exchange.FILE_NAME;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WhatsAppDbRoute extends AbstractRouteBuilder {

  WhatsAppDbRoute(final ApplicationArguments arguments) {
    super(arguments);
  }

  @Override
  public void configure() throws Exception {
    final var outoutPath = getTargetPath();
    final var toFile = "file:".concat(outoutPath);

    onException(Exception.class)
        .onExceptionOccurred(onExceptionProcessor())
        .continued(true)
        .maximumRedeliveries(0);

    from("direct:whatsAppDb")
        .setHeader("contentTypePath", constant("Databases"))
        .process(tikaProcessor())
        .process(fileMetadataProcessor())
        .log("[${file:name}][ContentType: ${in.header['CamelFileMediaType']}]")
        .setHeader(FILE_NAME, header("copyFileName"))
        .to(toFile)
        .setBody(header("fileMetadata"))
        .setHeader(FILE_NAME, header("fileMetadataJsonFileName"))
        .marshal().json()
        .to(toFile)
        .setBody(header("TikaText"))
        .setHeader(FILE_NAME, header("tikaTextFileName"))
        .to(toFile)
        .process(whatsAppDbProcessor())
        .to("file:/");
  }

  private Processor whatsAppDbProcessor() {
    return e -> {
      final var pipelinePath = getOptionValue("pipeline", "/pipeline");
      final var inputPath = Path.of(pipelinePath, "/input").toString();
      final var dbFilePath = e.getIn().getHeader(Exchange.FILE_PATH, String.class);
      final var relativeInputPath = dbFilePath.replaceAll(inputPath, "");
      final var pathSegments = Arrays.asList(relativeInputPath.split(FileSystems.getDefault().getSeparator()));
      final var dossierId = pathSegments.get(2);
      final var deviceId = pathSegments.get(3);
      final var fileName = FilenameUtils.getName(dbFilePath);
      final var dossierPath = String.join(File.separator, List.of(dossierId));
      final var devicePath = String.join(File.separator, List.of(deviceId));
      final var copyFileName = Path.of(pipelinePath, "Process", "Databases", "Whatsapp", dossierPath, devicePath, fileName).toString();
      e.getIn().setHeader(FILE_NAME, copyFileName);
    };
  }

}
