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
@Table(name = "user")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "nickname", nullable = false)
    private String nickname;
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    // 정확한 지역명을 위해 enum(권역별) -> String(지역별) 수정
    @Column(length = 10)
    private String countyCode;


    // 사용자 관심 품목
    private String interestItem;

}
