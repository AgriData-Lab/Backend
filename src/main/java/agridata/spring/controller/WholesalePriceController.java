package agridata.spring.controller;

import agridata.spring.dto.response.WholdesalePriceResponseDTO;
import agridata.spring.global.ApiResponse;
import agridata.spring.service.WholesalePriceApiService;
import agridata.spring.service.util.KamisCodeLoader;
import agridata.spring.service.util.KamisCodeMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WholesalePriceController {
    private final ObjectMapper mapper = new ObjectMapper();

    private final WholesalePriceApiService WholesalePriceApiService;
    private final KamisCodeLoader kamisCodeLoader;

    /**
     * 검색 기능
     * param: 품목, 지역, 시작일, 마지막일
     *
     * return:   "itemname": "오이",
     *       "kindname": "가시계통(10개)",
     *       "countyname": "서울",
     *       "marketname": "I-유통",
     *       "yyyy": "2025",
     *       "regday": "02/18",
     *       "price": "23,200"
     *       리스트
     * */
    @Operation(summary = "도매 가격 불러오기 API(품목 조회하기)", description = "도매 가격 데이터를 조회합니다. 품목, 지역(코드), 시작일, 마지막일을 받아 도매 가격 리스트를 반환합니다.")
    @GetMapping("/hipping-periods")
    public ApiResponse<List<WholdesalePriceResponseDTO.BasicDTO>> getWholesalePrice(
            @RequestParam String itemName,
            @RequestParam(defaultValue = "1101") String countryCode,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        KamisCodeMapper.KamisCode code = kamisCodeLoader.getCode(itemName);
        if (code == null) {
            log.warn("지원하지 않는 품목명: '{}'", itemName);
            return ApiResponse.onFailure("404", "지원하지 않는 품목명입니다: " + itemName, null);
        }

        log.info("✅ 매핑된 코드: {}", code);

        String xmlResponse = WholesalePriceApiService.getPriceData(
                code.itemCode(), code.kindCode(), code.itemCategoryCode(), code.rankCode(),
                countryCode, startDate, endDate
        );

        log.debug("📄 응답 원문:\n{}", xmlResponse);

        try {
            return ApiResponse.onSuccess(parseRetailPrice(xmlResponse));
        } catch (Exception e) {
            log.error("XML 파싱 실패", e);
            return ApiResponse.onFailure("500", "XML 파싱 실패: " + e.getMessage(), null);
        }
    }


    private List<WholdesalePriceResponseDTO.BasicDTO> parseRetailPrice(String xml) {
        Document doc = Jsoup.parse(xml, "", org.jsoup.parser.Parser.xmlParser());

        String condition = getText(doc, "condition", "N/A");
        String message = getText(doc, "error_code", "N/A");
        log.info("📡 KAMIS 응답 상태: {}, 메시지: {}", condition, message);

        Elements items = doc.getElementsByTag("item");
        log.info("파싱된 item 개수: {}", items.size());

        List<WholdesalePriceResponseDTO.BasicDTO> resultList = new ArrayList<>();
        for (Element item : items) {
            String price = getTagText(item, "price");
            if (price == null || price.isBlank()) {
                log.debug("가격 누락 항목:\n{}", item.outerHtml());
                continue;
            }

            WholdesalePriceResponseDTO.BasicDTO dto = WholdesalePriceResponseDTO.BasicDTO.builder()
                    .itemname(getTagText(item, "itemname"))
                    .kindname(getTagText(item, "kindname"))
                    .countyname(getTagText(item, "countyname"))
                    .marketname(getTagText(item, "marketname"))
                    .yyyy(getTagText(item, "yyyy"))
                    .regday(getTagText(item, "regday"))
                    .price(price)
                    .build();

            resultList.add(dto);
        }

        log.info("최종 응답 항목 수: {}", resultList.size());
        return resultList;
    }

    private String getText(Document doc, String tag, String defaultValue) {
        Element el = doc.selectFirst(tag);
        return el != null ? el.text() : defaultValue;
    }

    private String getTagText(Element element, String tag) {
        Element el = element.selectFirst(tag);
        return el != null ? el.text() : null;
    }
}
