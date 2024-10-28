package ch.akros.kapo.service.android_sms;

import lombok.Data;

@Data
public class AndroidChat {
  private final String number;
  private final String content;

  public String fileName() {
    return String.format("%s.txt", number.replace("+", ""));
  }
}