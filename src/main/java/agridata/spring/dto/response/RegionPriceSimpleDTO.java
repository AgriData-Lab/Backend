package agridata.spring.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegionPriceSimpleDTO {
    private String countyname; // 지역명
    private String price;      // 가격
}
