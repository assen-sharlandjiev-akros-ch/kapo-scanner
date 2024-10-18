package ch.akros.kapo;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KapoScannerApplication implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(KapoScannerApplication.class);

  @Autowired
  AtomicBoolean scanComplete;

  public static void main(String[] args) {
    SpringApplication.run(KapoScannerApplication.class, args);
  }

  @Override
  public void run(String... arguments) throws Exception {
    while (!scanComplete.get()) {
      Thread.sleep(1000L);
    }
  }
}
