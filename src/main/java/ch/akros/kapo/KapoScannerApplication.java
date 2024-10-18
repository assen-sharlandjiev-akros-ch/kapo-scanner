package ch.akros.kapo;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KapoScannerApplication implements CommandLineRunner {

  @Autowired
  AtomicBoolean scanComplete;

  public static void main(final String[] args) {
    SpringApplication.run(KapoScannerApplication.class, args);
  }

  @Override
  public void run(final String... arguments) throws Exception {
    while (!scanComplete.get()) {
      Thread.sleep(1000L);
    }
  }
}
