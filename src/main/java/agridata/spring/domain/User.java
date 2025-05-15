package agridata.spring.domain;

import agridata.spring.domain.common.BaseEntity;
import agridata.spring.domain.enums.Region;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "User")
@Data
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String password;
    private String nickname;
    private String email;
    private Boolean isVerified;

    @Enumerated(EnumType.STRING)
    private Region region;

    private Long interestItem;

}
