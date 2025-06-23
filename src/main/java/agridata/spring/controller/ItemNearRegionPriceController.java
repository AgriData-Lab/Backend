package agridata.spring.controller;

import agridata.spring.dto.response.NearRegionPriceDTO;
import agridata.spring.global.ApiResponse;
import agridata.spring.service.UserQueryService;
import agridata.spring.service.impl.NearRegionPriceServiceImpl;
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
@RequestMapping("/api/near-region/price")
public class ItemNearRegionPriceController {

    private final ObjectMapper mapper = new ObjectMapper();
    private final NearRegionPriceServiceImpl nearRegionPriceService;
    private final KamisCodeLoader kamisCodeLoader;
    private final UserQueryService userQueryService;

    /**
     * 지역별 가격 데이터 XML 파싱
     */
    private List<NearRegionPriceDTO.BasicDTO> parseRegionPrice(String xml, String name) {
        Document doc = Jsoup.parse(xml, "", org.jsoup.parser.Parser.xmlParser());
        Elements items = doc.getElementsByTag("item");

        List<NearRegionPriceDTO.BasicDTO> resultList = new ArrayList<>();
        for (Element item : items) {
            String price = getTagText(item, "price");
            String countyName = getTagText(item, "countyname");
            String weekprice = getTagText(item, "weekprice");
            String monthprice = getTagText(item, "monthprice");
            String yearprice = getTagText(item, "yearprice");

            String itemName = name;

            // price가 없으면 스킵
            if (price == null || price.isBlank()) {
                continue;
            }

            if (itemName == null || itemName.isBlank()) {
                itemName = "(품목 없음)";
            }


            resultList.add(NearRegionPriceDTO.BasicDTO.builder()
                    .itemName(itemName)
                    .countyName(countyName)
                    .price(price)
                    .weekprice(weekprice)
                    .monthprice(monthprice)
                    .yearprice(yearprice)
                    .build());
        }

        return resultList;
    }

    /**
     * 지역별 가격 정보 조회 API
     */
    @Operation(summary = "관심 품목의 지역별 가격 리스트 조회", description = "관심 품목에 대해 신청일자 기준으로 지역별 가격, 주간/월간/년간 변동 가격을 반환합니다.")
    @GetMapping("/by-region")
    public ApiResponse<List<NearRegionPriceDTO.BasicDTO>> getRegionPriceList(
            @RequestParam(defaultValue = "쌀") String itemName,
            @RequestParam(defaultValue = "") String countryCode,
            @RequestParam String startDate
    ) {

        KamisCodeMapper.KamisCode code = kamisCodeLoader.getCode(itemName);

        if (code == null) {
            log.warn("지원하지 않는 품목명: '{}'", itemName);
            return ApiResponse.onFailure("404", "지원하지 않는 품목명입니다: " + itemName, null);
        }

        log.info("✅ 매핑된 코드: {}", code);

        String xmlResponse = nearRegionPriceService.getPriceData(
                code.itemCode(),
                code.kindCode(),
                code.itemCategoryCode(),
                code.rankCode(),
                countryCode,
                startDate
        );

        log.debug("📄 수신된 XML:\n{}", xmlResponse);

        try {
            List<NearRegionPriceDTO.BasicDTO> parsed = parseRegionPrice(xmlResponse, itemName);
            return ApiResponse.onSuccess(parsed);
        } catch (Exception e) {
            log.error("XML 파싱 실패", e);
            return ApiResponse.onFailure("500", "XML 파싱 실패: " + e.getMessage(), null);
        }
    }

    private String getTagText(Element element, String tag) {
        Element el = element.selectFirst(tag);
        return el != null ? el.text() : null;
    }
}
