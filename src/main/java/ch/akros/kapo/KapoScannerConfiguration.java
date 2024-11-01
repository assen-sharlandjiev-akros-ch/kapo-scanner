package ch.akros.kapo;

import java.nio.file.Path;
import java.util.Optional;

import org.apache.camel.spi.IdempotentRepository;
import org.apache.camel.support.processor.idempotent.FileIdempotentRepository;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KapoScannerConfiguration {

  @Bean
  IdempotentRepository mapIdempotentRepository() {
    return new MapIdempotentRepository();
  }

  @Bean
  IdempotentRepository memoryIdempotentRepository() {
    return MemoryIdempotentRepository.memoryIdempotentRepository(Integer.MAX_VALUE);
  }

  @Bean
  IdempotentRepository fileIdempotentRepository(final ApplicationArguments arguments) {
    final var sourcePath = Optional.ofNullable(arguments.getOptionValues("source")).map(l -> l.getFirst()).orElse("/source");
    final var file = Path.of("/", FilenameUtils.getPath(sourcePath), "idempotentRepository.txt").toFile();
    return FileIdempotentRepository.fileIdempotentRepository(file, 50_000);
  }

}
