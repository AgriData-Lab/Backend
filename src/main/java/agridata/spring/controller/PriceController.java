package agridata.spring.controller;

import agridata.spring.dto.response.KamisResponseDTO;
import agridata.spring.global.ApiResponse;
import agridata.spring.service.KamisApiService;
import agridata.spring.service.util.KamisCodeLoader;
import agridata.spring.service.util.KamisCodeMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/kamis") // /retail -> /kamis로 변경 (Kamis 파일들은 OpenAPI 기본 틀로 놔둘 예정)
public class PriceController {

    private final KamisApiService kamisApiService;
    private final KamisCodeLoader kamisCodeLoader;

    @Operation(summary = "소매 가격 불러오기 API", description = "소매 가격 불러오기 API입니다.")
    @GetMapping
    public ApiResponse<List<KamisResponseDTO.KamisRetailDTO>> getRetailPrice(
            @RequestParam String itemName,
            @RequestParam(defaultValue = "1101") String countryCode,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        log.info("📥 소매 가격 조회 요청: itemName={}, countryCode={}, startDate={}, endDate={}",
                itemName, countryCode, startDate, endDate);

        KamisCodeMapper.KamisCode code = kamisCodeLoader.getCode(itemName);
        if (code == null) {
            log.warn("❌ itemName 매핑 실패: '{}'", itemName);
            return ApiResponse.onFailure("404", "지원하지 않는 품목명입니다: " + itemName, null);
        }

        log.info("✅ 매핑된 코드: itemCode={}, kindCode={}, categoryCode={}, rankCode={}",
                code.itemCode(), code.kindCode(), code.itemCategoryCode(), code.rankCode());

        String xmlResponse = kamisApiService.getPriceData(
                code.itemCode(), code.kindCode(), code.itemCategoryCode(), code.rankCode(),
                countryCode, startDate, endDate
        );

        // ✅ 응답 원문 로그 출력
        log.debug("📄 응답 원문:\n{}", xmlResponse);

        try {
            Document doc = Jsoup.parse(xmlResponse, "", org.jsoup.parser.Parser.xmlParser());

            String condition = doc.selectFirst("condition") != null ? doc.selectFirst("condition").text() : "N/A";
            String message = doc.selectFirst("error_code") != null ? doc.selectFirst("error_code").text() : "N/A";
            log.info("📡 KAMIS 응답 상태: {}, 메시지: {}", condition, message);

            Elements items = doc.getElementsByTag("item");
            log.info("📦 파싱된 item 개수: {}", items.size());

            List<KamisResponseDTO.KamisRetailDTO> resultList = new ArrayList<>();

            for (Element item : items) {
                String price = getTagText(item, "price");
                if (price == null || price.isBlank()) {
                    log.debug("⛔️ price 누락: {}", item.outerHtml());
                    continue;
                }

                String itemname = getTagText(item, "itemname");
                if (itemname == null || itemname.isBlank()) {
                    log.warn("⚠️ itemname 누락 item 존재: {}", item.outerHtml());
                }

                KamisResponseDTO.KamisRetailDTO dto = KamisResponseDTO.KamisRetailDTO.builder()
                        .itemname(itemname)
                        .kindname(getTagText(item, "kindname"))
                        .countyname(getTagText(item, "countyname"))
                        .marketname(getTagText(item, "marketname"))
                        .yyyy(getTagText(item, "yyyy"))
                        .regday(getTagText(item, "regday"))
                        .price(price)
                        .build();
                resultList.add(dto);
            }

            log.info("✅ 최종 응답 항목 수: {}", resultList.size());
            return ApiResponse.onSuccess(resultList);
        } catch (Exception e) {
            log.error("❌ XML 파싱 실패", e);
            return ApiResponse.onFailure("500", "XML 파싱 실패: " + e.getMessage(), null);
        }
    }

    private String getTagText(Element element, String tag) {
        Element el = element.selectFirst(tag);
        return el != null ? el.text() : null;
    }
}
