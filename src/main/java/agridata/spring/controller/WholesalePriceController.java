package agridata.spring.controller;

import agridata.spring.dto.response.KamisResponseDTO;
import agridata.spring.global.ApiResponse;
import agridata.spring.service.KamisApiService;
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
@RequestMapping("/api/wholesale-price")
public class WholesalePriceController {
    private final ObjectMapper mapper = new ObjectMapper();

    private final KamisApiService kamisApiService;
    private final KamisCodeLoader kamisCodeLoader;

    @Operation(summary = "도매 가격 불러오기 API", description = "도매 가격 데이터를 조회합니다.")
    @GetMapping
    public ApiResponse<List<KamisResponseDTO.KamisRetailDTO>> getRetailPrice(
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

        String xmlResponse = kamisApiService.getPriceData(
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

    private List<KamisResponseDTO.KamisRetailDTO> parseRetailPrice(String xml) {
        Document doc = Jsoup.parse(xml, "", org.jsoup.parser.Parser.xmlParser());

        String condition = getText(doc, "condition", "N/A");
        String message = getText(doc, "error_code", "N/A");
        log.info("📡 KAMIS 응답 상태: {}, 메시지: {}", condition, message);

        Elements items = doc.getElementsByTag("item");
        log.info("파싱된 item 개수: {}", items.size());

        List<KamisResponseDTO.KamisRetailDTO> resultList = new ArrayList<>();
        for (Element item : items) {
            String price = getTagText(item, "price");
            if (price == null || price.isBlank()) {
                log.debug("가격 누락 항목:\n{}", item.outerHtml());
                continue;
            }

            KamisResponseDTO.KamisRetailDTO dto = KamisResponseDTO.KamisRetailDTO.builder()
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
