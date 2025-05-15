package agridata.spring.domain;

import agridata.spring.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "Price")
@Data
public class Price extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kind_id")
    private Kind kind;

    private LocalDate date;
    private Integer price;
}

