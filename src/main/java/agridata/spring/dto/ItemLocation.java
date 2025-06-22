package agridata.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemLocation {
    private String city;
    private String item;
    private Double longitude;
    private Double latitude;
}
