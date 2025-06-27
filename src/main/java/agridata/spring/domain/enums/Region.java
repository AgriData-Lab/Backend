package agridata.spring.domain.enums;

import java.util.List;


// 일단 참고 차 냅두기
public enum Region {
    수도권(List.of("1101", "2300", "3111", "3112", "3113", "3138", "3145")), // 서울, 인천, 수원 등
    관동권(List.of("3211", "3214")), // 춘천, 강릉
    호서권(List.of("2501", "3311", "3411", "2701")), // 대전, 청주, 천안, 세종
    호남권(List.of("2401", "3511", "3613")), // 광주, 전주, 순천
    영남권(List.of("2100", "2200", "2601", "3711", "3714", "3814", "3818")), // 부산, 대구, 울산 등
    제주권(List.of("3911")); // 제주

    private final List<String> countyCodes;

    Region(List<String> countyCodes) {
        this.countyCodes = countyCodes;
    }

    public List<String> getCountyCodes() {
        return countyCodes;
    }
}
