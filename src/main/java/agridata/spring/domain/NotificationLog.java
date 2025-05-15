package agridata.spring.domain;

import agridata.spring.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "NotificationLog")
@Data
public class NotificationLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationLogId;

    private LocalDateTime triggeredAt;
    private Integer currentPrice;
    private String message;
    private String field;
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id")
    private Notification notification;
}

