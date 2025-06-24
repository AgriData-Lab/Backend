package agridata.spring.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// Retail(소매) 응답 DTO 입니다.
public class RetailPriceResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RetailBasicDTO {
        private String itemname;     // 품목명
        private String kindname;     // 품종명
        private String countyname;   // 시군구
        private String marketname;   // 마켓명
        private String yyyy;         // 연도
        private String regday;       // 날짜
        private String price;        // 가격
    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    // 응답이 배열 형태라면 리스트로 감싸야 하므로, 다음처럼도 사용 가능
    public static class KamisRetailWrapperDTO {
        private List<KamisResponseDTO.KamisRetailDTO> data;
    }

}
