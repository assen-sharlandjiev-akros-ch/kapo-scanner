package ch.akros.kapo.service.iphone_sms;

import lombok.Data;

@Data
public class IphoneChat {
  private final String from;
  private final String to;
  private final String content;

  public String fileName() {
    return String.format("%s-%s.txt", from, to);
  }
}
