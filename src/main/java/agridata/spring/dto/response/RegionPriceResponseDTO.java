package agridata.spring.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class RegionPriceResponseDTO {

    @Builder
    @Getter
    public static class BasicDTO {
        private String condition;     // 요청 메세지
        private String data;     // 응답 메세지
        private String item;   // 조회 목록
        private String countyname;   // 시군구
        private String unit;         // 단위
        private String price;       // 조회일자 가격
        private String weekprice;        // 1개월전 가격
        private String monthprice;        // 1개월전 가격
        private String yearprice;        // 1년전 가격
    }

    @Builder
    @Getter
    // 응답이 배열 형태라면 리스트로 감싸야 하므로, 다음처럼도 사용 가능
    public static class ListWrapperDTO {
        private List<BasicDTO> data;
    }
}