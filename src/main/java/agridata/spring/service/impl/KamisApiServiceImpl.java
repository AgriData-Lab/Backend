package agridata.spring.service.impl;

import agridata.spring.service.KamisApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KamisApiServiceImpl implements KamisApiService {

    private final RestTemplate restTemplate;

    @Override
    public String getPriceData(String itemCode, String kindCode, String itemCategoryCode, String rankCode, String countryCode,
                               String startDate, String endDate) {
        String apiKey = "a39d2458-c37a-4d18-b0bb-1de076593317"; // 발급받은 KEY
        String certId = "5866"; // 발급받은 ID

        StringBuilder urlBuilder = new StringBuilder("https://www.kamis.or.kr/service/price/xml.do"); // http -> https
        urlBuilder.append("?action=periodProductList")
                .append("&p_cert_key=").append(apiKey)
                .append("&p_cert_id=").append(certId)
                .append("&p_returntype=xml")
                .append("&p_productclscode=01") // 소매
                .append("&p_itemcategorycode=").append(itemCategoryCode)
                .append("&p_itemcode=").append(itemCode)
                .append("&p_countrycode=").append(countryCode)
                .append("&p_convert_kg_yn=Y")
                .append("&p_startday=").append(formatDate(startDate))
                .append("&p_endday=").append(formatDate(endDate));

        // 선택적 파라미터: 비어있지 않은 경우에만 추가
        if (kindCode != null && !kindCode.isBlank()) {
            urlBuilder.append("&p_kindcode=").append(kindCode);
        }
        if (rankCode != null && !rankCode.isBlank()) {
            urlBuilder.append("&p_productrankcode=").append(rankCode);
        }

        String url = urlBuilder.toString();
        log.info("📡 KAMIS 요청 URL: {}", url);

        // ✅ User-Agent 설정해서 요청
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0");  // 브라우저처럼 보이도록

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }

    private String formatDate(String yyyymmdd) {
        return yyyymmdd.substring(0, 4) + "-" + yyyymmdd.substring(4, 6) + "-" + yyyymmdd.substring(6, 8);
    }
}
