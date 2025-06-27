package agridata.spring.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class NotificationRequestDTO {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateRequest {
        //private Long kindId;
        private String itemName;
        private Integer targetPrice;
        private String type; // 도매 or 소매
        private Boolean isActive;
        private String countyCode; // 추가
    }
}
