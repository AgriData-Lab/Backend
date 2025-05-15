package agridata.spring.domain;

import agridata.spring.domain.common.BaseEntity;
import agridata.spring.domain.enums.Type;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Notification")
@Data
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private Long productId;
    private Integer targetPrice;
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kind_id")
    private Kind kind;

}

