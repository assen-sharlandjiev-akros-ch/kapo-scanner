package ch.akros.kapo;

import static java.lang.Integer.MAX_VALUE;
import static org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository.memoryIdempotentRepository;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.camel.spi.IdempotentRepository;
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

}
