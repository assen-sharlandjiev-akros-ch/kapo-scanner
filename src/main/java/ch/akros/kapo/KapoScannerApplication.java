package ch.akros.kapo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KapoScannerApplication implements CommandLineRunner {

  public static void main(final String[] args) {
    SpringApplication.run(KapoScannerApplication.class, args);
  }

  @Override
  public void run(final String... arguments) throws Exception {
  }
}
