package agridata.spring.domain;

import agridata.spring.domain.common.BaseEntity;
import agridata.spring.domain.enums.Type;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "Notification")
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private String itemName;
    private Integer targetPrice;
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Type type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 지역 추가
    @Column(length = 10)
    private String countyCode;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "kind_id")
//    private Kind kind;

}

