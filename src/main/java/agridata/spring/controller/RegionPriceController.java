package agridata.spring.controller;

import agridata.spring.dto.response.RegionPriceSimpleDTO;
import agridata.spring.global.ApiResponse;
import agridata.spring.service.RegionPriceService;
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
@RequestMapping("/api/prices-distribution")
public class RegionPriceController {
    private final ObjectMapper mapper = new ObjectMapper();

    private final RegionPriceService regionPriceService;
    private final KamisCodeLoader kamisCodeLoader;

    private List<RegionPriceSimpleDTO> parseSimplePrice(String xml) {
        Document doc = Jsoup.parse(xml, "", org.jsoup.parser.Parser.xmlParser());
        Elements items = doc.getElementsByTag("item");

        List<RegionPriceSimpleDTO> resultList = new ArrayList<>();
        for (Element item : items) {
            String county = getTagText(item, "countyname");
            String price = getTagText(item, "price");

            // "등락률"이나 price가 비어있을 경우 제외
            if (county == null || county.contains("등락률") || price == null || price.isBlank()) {
                continue;
            }

            resultList.add(new RegionPriceSimpleDTO(county, price));
        }

        return resultList;
    }


    /**
     * 검색 기능
     */
    // Todo 랭크 관련 문제 해결
    @Operation(summary = "관심품목의 전국 시세 불러오기 API(도매)", description = "관심품목의 전국 시세 불러오기 API(도매). 관심품목은 백엔드에서 처리합니다.")
    @GetMapping("/hipping-periods")
    public ApiResponse<List<RegionPriceSimpleDTO>> getWholesalePrice(
            @RequestParam String itemName,
            @RequestParam(defaultValue = "") String countryCode,
            @RequestParam String startDate
    ) {
        KamisCodeMapper.KamisCode code = kamisCodeLoader.getCode(itemName);
        if (code == null) {
            log.warn("지원하지 않는 품목명: '{}'", itemName);
            return ApiResponse.onFailure("404", "지원하지 않는 품목명입니다: " + itemName, null);
        }

        log.info("✅ 매핑된 코드: {}", code);

        String xmlResponse = regionPriceService.getPriceData(
                code.itemCode(),
                code.kindCode(),
                code.itemCategoryCode(),
                code.rankCode(),
                countryCode,
                startDate // 시작일을 기준일로 사용
        );



        log.debug("📄 응답 원문:\n{}", xmlResponse);

        try {
            return ApiResponse.onSuccess(parseSimplePrice(xmlResponse));
        } catch (Exception e) {
            log.error("XML 파싱 실패", e);
            return ApiResponse.onFailure("500", "XML 파싱 실패: " + e.getMessage(), null);
        }
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
