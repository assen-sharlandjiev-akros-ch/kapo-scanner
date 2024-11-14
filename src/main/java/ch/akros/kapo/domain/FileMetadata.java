package ch.akros.kapo.domain;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FileMetadata {
  private String id;
  private String fileName;
  @JsonProperty("dossier_id")
  private String dossierId;
  @JsonProperty("device_id")
  private String deviceId;
  private String location;
  private Long size;
  private Instant creationDateTime;
  private Instant accessDateTime;
  private Instant changeDateTime;
  private Map<String, Object> properties;
}
