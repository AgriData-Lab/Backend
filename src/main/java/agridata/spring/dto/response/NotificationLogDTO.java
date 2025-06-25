package agridata.spring.dto.response;

import agridata.spring.domain.NotificationLog;
import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
@Builder
public class NotificationLogDTO {
    private String itemName;
    private String message;
    private String triggeredAt;
    private String type;
    private Integer currentPrice;
    private Long notificationId;

    public static NotificationLogDTO from(NotificationLog log) {
        return NotificationLogDTO.builder()
                .itemName(log.getField())
                .message(log.getMessage())
                .triggeredAt(log.getTriggeredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .type(log.getType())
                .currentPrice(log.getCurrentPrice())
                .notificationId(log.getNotification().getNotificationId())
                .build();
    }
}

