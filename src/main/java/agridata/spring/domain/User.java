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
@Data // 이 어노테이션 왜 있는 거임..?
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String nickname;
    private String password;

    private String email;

    // 이 필드 왜 있는 거임..?
    private Boolean isVerified;

    // 사용자 지역
    @Enumerated(EnumType.STRING)
    private Region region;

    // 사용자 관심 품목
    private String interestItem;

}
