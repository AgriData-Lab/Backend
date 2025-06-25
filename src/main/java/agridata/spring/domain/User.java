package agridata.spring.domain;

import agridata.spring.domain.common.BaseEntity;
import agridata.spring.domain.enums.Region;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "User")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String nickname;
    private String password;

    @Column(nullable = false)
    private String email;

    // 사용자 지역
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private Region region;

    // 사용자 관심 품목
    private String interestItem;

}
