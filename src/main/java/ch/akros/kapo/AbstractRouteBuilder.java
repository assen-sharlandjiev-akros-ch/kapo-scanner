package ch.akros.kapo;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;
import static org.apache.camel.Exchange.FILE_NAME;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getFullPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.boot.ApplicationArguments;

public abstract class AbstractRouteBuilder extends RouteBuilder {

  private final ApplicationArguments arguments;
  protected Tika tika;
  protected JacksonDataFormat jacksonDataFormat;

  AbstractRouteBuilder(final ApplicationArguments arguments) {
    this.arguments = arguments;
    try {
      this.tika = new Tika();
    } catch (final Exception e) {
      log.error("Failed to instantiate Tika");
    }
    jacksonDataFormat = new JacksonDataFormat();
    jacksonDataFormat.setModuleClassNames("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule");
    jacksonDataFormat.setAutoDiscoverObjectMapper(true);
  }

  protected List<String> getNonOptionArgs() {
    return arguments.getNonOptionArgs();

  }

  protected List<String> getOptionValues(final String name) {
    return arguments.getOptionValues(name);
  }

  protected String getOptionValue(final String name, final String defaultValue) {
    return Optional.ofNullable(arguments.getOptionValues(name)).map(l -> l.getFirst()).orElse(defaultValue);
  }

  protected String getOutputPath(final String contentType) throws IOException {
    final var outoutPath = Path.of(getOptionValue("target", "/target"), contentType);
    if (!outoutPath.toFile().exists()) {
      Files.createDirectories(outoutPath);
    }
    return outoutPath.toString();
  }

  protected Processor tikaProcessor() {
    return e -> {
      final var file = ((File) e.getIn().getBody(GenericFile.class).getFile());
      final var parser = new AutoDetectParser();
      final var handler = new BodyContentHandler(Integer.MAX_VALUE);
      final var metadata = new Metadata();
      if (nonNull(tika)) {
        try (final var fos = new FileInputStream(file)) {
          parser.parse(fos, handler, metadata);
          final var text = handler.toString();
          e.getIn().setHeader("TikaText", text);
          e.getIn().setHeader("TikaMetadata", metadata);
        } catch (final Exception ex) {
          log.error("[TEXT][Fialed to extract text from: {}][Error: {}]", file.getAbsolutePath(), ex.getMessage());
        }
      }
    };
  }

  protected Processor fileMetadataProcessor() {
    return e -> {
      final var file = ((File) e.getIn().getBody(GenericFile.class).getFile());
      final var attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
      final var tikaMedadata = e.getIn().getHeader("TikaMetadata", Metadata.class);
      final var fileMetadata = new FileMetadata();
      final var fileName = e.getIn().getHeader(FILE_NAME, String.class);
      final var fileMetadataJsonFileName = getFullPath(fileName).concat(getBaseName(fileName)).concat(".json");
      final var tikaTextFileName = getFullPath(fileName).concat(getBaseName(fileName)).concat(".txt");
      final var tikaMedadataHashMap = Arrays.stream(tikaMedadata.names())
        .collect(toMap(Function.identity(), tikaMedadata::get));
      fileMetadata.setId(e.getExchangeId());
      fileMetadata.setFileName(e.getIn().getHeader(Exchange.FILE_NAME_ONLY, String.class));
      fileMetadata.setLocation(e.getIn().getHeader(Exchange.FILE_PATH, String.class));
      fileMetadata.setSize(e.getIn().getHeader(Exchange.FILE_LENGTH, Long.class));
      fileMetadata.setCreationDateTime(attr.creationTime().toInstant());
      fileMetadata.setChangeDateTime(attr.lastModifiedTime().toInstant());
      fileMetadata.setAccessDateTime(attr.lastAccessTime().toInstant());
      fileMetadata.setProperties(Map.of("tikaMetadata", tikaMedadataHashMap));
      e.getIn().setHeader("fileMetadata", fileMetadata);
      e.getIn().setHeader("fileMetadataJsonFileName", fileMetadataJsonFileName);
      e.getIn().setHeader("tikaTextFileName", tikaTextFileName);
    };
  }
}
