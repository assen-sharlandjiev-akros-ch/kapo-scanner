package ch.akros.kapo;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix="kapo")
@Data
public class KapoConfigurationProperties {
  private List<String> documentContentTypes;
}
