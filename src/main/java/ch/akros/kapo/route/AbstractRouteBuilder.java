package ch.akros.kapo.route;

import static java.util.stream.Collectors.toMap;
import static org.apache.camel.Exchange.EXCEPTION_CAUGHT;
import static org.apache.camel.Exchange.FILE_NAME;
import static org.apache.commons.io.FilenameUtils.getBaseName;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.boot.ApplicationArguments;

import ch.akros.kapo.domain.FileMetadata;

public abstract class AbstractRouteBuilder extends RouteBuilder {

  private final ApplicationArguments arguments;
  protected Tika tika;
  protected JacksonDataFormat jacksonDataFormat;

  AbstractRouteBuilder(final ApplicationArguments arguments) {
    this.arguments = arguments;
    this.tika = new Tika();
    this.jacksonDataFormat = new JacksonDataFormat();
    this.jacksonDataFormat.setModuleClassNames("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule");
    this.jacksonDataFormat.setAutoDiscoverObjectMapper(true);
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

  protected String getTargetPath() {
    final var pipelinePath = getOptionValue("pipeline", "/pipeline");
    return Path.of(pipelinePath, "/output").toString();
  }

  protected String getTargetPath(final String contentType) throws IOException {
    final var outoutPath = Path.of(getTargetPath(), contentType);
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
      try (final var fos = new FileInputStream(file)) {
        parser.parse(fos, handler, metadata);
        final var text = handler.toString();
        e.getIn().setHeader("TikaText", text);
        e.getIn().setHeader("TikaMetadata", metadata);
      } catch (final Exception ex) {
        log.error("[TEXT][Fialed to extract text from: {}][Error: {}]", file.getAbsolutePath(), ex.getCause().getMessage());
        throw ex;
      }
    };
  }

  protected Processor fileMetadataProcessor() {
    return e -> {
      final var pathSeparator = FileSystems.getDefault().getSeparator();
      final var sourcePath = Path.of(getOptionValue("pipeline", "/pipeline"), "/input").toString();
      final var contentTypePath = e.getIn().getHeader("contentTypePath", String.class);
      final var relativeSourcePath = e.getIn().getHeader(Exchange.FILE_PATH, String.class).replaceAll(sourcePath, "");
      final var pathSegments = Arrays.asList(relativeSourcePath.split(pathSeparator));
      final var dossierDevicePath = String.join(File.separator, List.of(pathSegments.get(2))); // , pathSegments.get(2)));
      final var uuid = UUID.randomUUID().toString();
      final var dir = uuid.substring(0, uuid.indexOf("-"));
      final var file = ((File) e.getIn().getBody(GenericFile.class).getFile());
      final var attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
      final var tikaMedadata = e.getIn().getHeader("TikaMetadata", Metadata.class);
      final var fileMetadata = new FileMetadata();
      final var fileName = e.getIn().getHeader(FILE_NAME, String.class);
      final var filePathPrefix =  String.format("/%s/%s/%s/%s", dossierDevicePath, contentTypePath, dir, uuid);
      final var copyFileName = String.format("/%s/%s/%s/%s_%s", dossierDevicePath, contentTypePath, dir, uuid, FilenameUtils.getName(fileName));
      final var fileMetadataJsonFileName = String.format("/%s/%s/%s/metadata/%s_%s", dossierDevicePath, contentTypePath, dir, uuid, FilenameUtils.getName(fileName).concat(".json"));
      final var tikaTextFileName = String.format("/%s/%s/%s/%s_%s", dossierDevicePath, contentTypePath, dir, uuid, getBaseName(fileName)).concat(".txt");

      fileMetadata.setId(e.getExchangeId());
      fileMetadata.setFileName(e.getIn().getHeader(Exchange.FILE_NAME_ONLY, String.class));
      fileMetadata.setLocation(e.getIn().getHeader(Exchange.FILE_PATH, String.class));
      fileMetadata.setSize(e.getIn().getHeader(Exchange.FILE_LENGTH, Long.class));
      fileMetadata.setCreationDateTime(attr.creationTime().toInstant());
      fileMetadata.setChangeDateTime(attr.lastModifiedTime().toInstant());
      fileMetadata.setAccessDateTime(attr.lastAccessTime().toInstant());
      if (Objects.nonNull(tikaMedadata)) {
        final Map<String, Object> tikaMedadataHashMap = Arrays.stream(tikaMedadata.names()).collect(toMap(Function.identity(), tikaMedadata::get));
        fileMetadata.setProperties(tikaMedadataHashMap);
      }

      e.getIn().setHeader("filePathPrefix", filePathPrefix);
      e.getIn().setHeader("copyFileName", copyFileName);
      e.getIn().setHeader("fileMetadata", fileMetadata);
      e.getIn().setHeader("fileMetadataJsonFileName", fileMetadataJsonFileName);
      e.getIn().setHeader("tikaTextFileName", tikaTextFileName);
    };
  }

  protected Processor onExceptionProcessor() {
    return exchange -> {
      final var ex = exchange.getProperty(EXCEPTION_CAUGHT, Exception.class);
      final var file = exchange.getIn().getHeader(Exchange.FILE_NAME);
      log.error("[{}][{}][{}}", file, ex.getClass(), ex.getMessage());
    };
  }
}
