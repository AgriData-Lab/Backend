package agridata.spring.domain;

import agridata.spring.domain.common.BaseEntity;
import agridata.spring.domain.enums.Category;
import agridata.spring.domain.enums.Unit;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Kind")
@Data
public class Kind extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long kindId;

    private String kindName;

    @Enumerated(EnumType.STRING)
    private Category category;

    private Integer itemcode;

    @Enumerated(EnumType.STRING)
    private Unit unit;

}
