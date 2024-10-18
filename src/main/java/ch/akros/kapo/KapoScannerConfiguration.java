package ch.akros.kapo;

import static java.lang.Integer.MAX_VALUE;
import static org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository.memoryIdempotentRepository;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.camel.spi.IdempotentRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KapoScannerConfiguration {

  @Bean
  AtomicBoolean scanComplete() {
    return new AtomicBoolean(false);
  }

  @Bean
  IdempotentRepository repo() {
    return memoryIdempotentRepository(MAX_VALUE);
  }

  @Bean
  FileScannerRoute fileScannerRoute(final ApplicationArguments arguments, final AtomicBoolean scanComplete) {
    return new FileScannerRoute(arguments, scanComplete);
  }

  @Bean
  ImageRoute imageRoute(final ApplicationArguments arguments) {
    return new ImageRoute(arguments);
  }

  @Bean
  VideoRoute videoRoute(final ApplicationArguments arguments) {
    return new VideoRoute(arguments);
  }

  @Bean
  TextRoute textRoute(final ApplicationArguments arguments) {
    return new TextRoute(arguments);
  }

  @Bean
  SqlLite3Route sqlLite3Route(final ApplicationArguments arguments) {
    return new SqlLite3Route(arguments);
  }

  @Bean
  IphoneSmsDbRoute iphoneSmsDbRoute(final ApplicationArguments arguments) {
    return new IphoneSmsDbRoute(arguments);
  }
}
