package agridata.spring.controller;

import agridata.spring.dto.response.RetailPriceResponseDTO;
import agridata.spring.global.ApiResponse;
import agridata.spring.service.RetailPriceApiService;
import agridata.spring.service.util.KamisCodeLoader;
import agridata.spring.service.util.KamisCodeMapper;
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
@RequestMapping("/retail")
public class RetailPriceController {

    private final RetailPriceApiService retailPriceApiService;
    private final KamisCodeLoader kamisCodeLoader;

    @Operation(summary = "소매 가격 불러오기 API(품목 조회하기)", description = "소매 가격 데이터를 조회합니다. 품목, 지역(코드), 시작일, 마지막일을 받아 도매 가격 리스트를 반환합니다.")
    @GetMapping("/prices") // 품목별 소매 가격 조회
    public ApiResponse<List<RetailPriceResponseDTO.RetailBasicDTO>> getRetailPrice(
            @RequestParam String itemName,
            @RequestParam(defaultValue = "") String countryCode,
            @RequestParam String startDate,
            @RequestParam String endDate
    )

    {
        log.info("📥 소매 가격 조회 요청: itemName={}, countryCode={}, startDate={}, endDate={}",
                itemName, countryCode, startDate, endDate);

        KamisCodeMapper.KamisCode code = kamisCodeLoader.getCode(itemName);
        if (code == null) {
            log.warn("🥬 지원하지 않는 품목명: '{}'", itemName);
            return ApiResponse.onFailure("404", "지원하지 않는 품목명입니다: " + itemName, null);
        }

        log.info("✅ 매핑된 코드: itemCode={}, kindCode={}, categoryCode={}, rankCode={}",
                code.itemCode(), code.kindCode(), code.itemCategoryCode(), code.rankCode());

        String xmlResponse = retailPriceApiService.getPriceData(
                code.itemCode(), code.kindCode(), code.itemCategoryCode(), code.rankCode(),
                countryCode, startDate, endDate
        );

        // 응답 원문 로그(Xml - ver) 출력
        log.debug("📄 응답 원문:\n{}", xmlResponse);

        try {
            return ApiResponse.onSuccess(parseRetailPrice(xmlResponse));
        } catch (Exception e) {
            log.error("❌ XML 파싱 실패", e);
            return ApiResponse.onFailure("500", "XML 파싱 실패: " + e.getMessage(), null);
        }
    }

    private List<RetailPriceResponseDTO.RetailBasicDTO> parseRetailPrice(String xml){
        Document doc = Jsoup.parse(xml, "", org.jsoup.parser.Parser.xmlParser());

        String condition = getText(doc, "condition", "N/A");
        String message = getText(doc, "error_code", "N/A");
        log.info("📡 KAMIS 응답 상태: {}, 메시지: {}", condition, message);

        Elements items = doc.getElementsByTag("item");
        log.info("📦 파싱된 item 개수: {}", items.size());

        List<RetailPriceResponseDTO.RetailBasicDTO> resultList = new ArrayList<>();

        for (Element item : items) {
            String price = getTagText(item, "price");
            
            if (price == null || price.isBlank()) {
                log.debug("⛔️ price 누락 항목: {}", item.outerHtml());
                continue;
            }

            String itemname = getTagText(item, "itemname");
            if (itemname == null || itemname.isBlank()) {
                log.warn("⚠️ itemname 누락 항목 존재: {}", item.outerHtml());
            }

            String countyname = getTagText(item, "countyname");
            if(countyname == null || countyname.isBlank() || countyname.equals("평년") || countyname.equals("평균")) {
                log.debug("지역 누락 항목:\n{}", item.outerHtml());
                continue;
            }

            RetailPriceResponseDTO.RetailBasicDTO dto = RetailPriceResponseDTO.RetailBasicDTO.builder()
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
