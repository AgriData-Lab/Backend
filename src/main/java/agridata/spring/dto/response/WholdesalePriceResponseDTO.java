package agridata.spring.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class WholdesalePriceResponseDTO {

    @Builder
    @Getter
    public static class BasicDTO {
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
    // 응답이 배열 형태라면 리스트로 감싸야 하므로, 다음처럼도 사용 가능
    public static class ListWrapperDTO {
        private List<BasicDTO> data;
    }
}