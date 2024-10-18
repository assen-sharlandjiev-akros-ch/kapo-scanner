package ch.akros.kapo;

import java.time.Instant;
import java.util.Map;

import lombok.Data;

@Data
public class FileMetadata {
  private String id;
  private String fileName;
  private String location;
  private Long size;
  private Instant creationDateTime;
  private Instant accessDateTime;
  private Instant changeDateTime;
  private Map<String, Object> properties;
}
