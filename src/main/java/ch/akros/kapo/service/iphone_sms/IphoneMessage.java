package ch.akros.kapo.service.iphone_sms;

import lombok.Data;

@Data
public class IphoneMessage {

  private final String messageType;
  private final String messageContent;
  private final boolean isFromMe;
  private final String messageSentDate;

}
