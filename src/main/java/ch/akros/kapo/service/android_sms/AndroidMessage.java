package ch.akros.kapo.service.android_sms;

import lombok.Data;

@Data
public class AndroidMessage {

  private final String messageDirection;
  private final String messageContent;
  private final String messageDate;

}
