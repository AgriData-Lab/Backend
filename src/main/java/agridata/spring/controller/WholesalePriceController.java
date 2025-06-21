package agridata.spring.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wholesale-price")
public class WholesalePriceController {
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${WHOLESALE_SERVICE_KEY}")
    private String serviceKey;

    @Value("${WHOLESALE_SERVICE_ID}")
    private String serviceId;

    @GetMapping("/test")
    public JsonNode test() {
        try {
            String apiUrl = "http://www.kamis.or.kr/service/price/xml.do" +
                    "?action=periodProductList" +
                    "&p_productclscode=02" +
                    "&p_startday=2022-10-01" +
                    "&p_endday=2022-12-01" +
                    "&p_itemcategorycode=200" +
                    "&p_itemcode=212" +
                    "&p_kindcode=00" +
                    "&p_productrankcode=04" +
                    "&p_countrycode=1101" +
                    "&p_convert_kg_yn=Y" +
                    "&p_cert_key=" + serviceKey +
                    "&p_cert_id=" + serviceId +
                    "&p_returntype=json";

            return callKamisApi(apiUrl);
        } catch (Exception e) {
            throw new RuntimeException("도매 시세 테스트 요청 실패", e);
        }
    }

    private JsonNode callKamisApi(String urlString) throws Exception {
        System.out.println("KAMIS API 호출 URL: " + urlString);

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        InputStreamReader isr = new InputStreamReader(
                (responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream(),
                "UTF-8"
        );

        BufferedReader rd = new BufferedReader(isr);
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
        }
        rd.close();
        conn.disconnect();

        return mapper.readTree(response.toString());
    }

    @GetMapping
    public JsonNode getWholesalePrice(
            @RequestParam(defaultValue = "02") String p_productclscode,
            @RequestParam(defaultValue = "200") String p_itemcategorycode,
            @RequestParam(defaultValue = "212") String p_itemcode,
            @RequestParam(defaultValue = "00") String p_kindcode,
            @RequestParam(defaultValue = "04") String p_productrankcode,
            @RequestParam(defaultValue = "1101") String p_countrycode,
            @RequestParam(defaultValue = "2025-06-10") String p_startday,
            @RequestParam(defaultValue = "2025-06-20") String p_endday
    ) {
        try {
            String serviceKey = "여기에_실제_API_KEY";
            String serviceId = "여기에_실제_사용자_ID";

            String apiUrl = "http://www.kamis.or.kr/service/price/xml.do?action=periodProductList";

            StringBuilder urlBuilder = new StringBuilder(apiUrl);
            urlBuilder.append("?p_productclscode=").append(p_productclscode);
            urlBuilder.append("&p_startday=").append(p_startday);
            urlBuilder.append("&p_endday=").append(p_endday);
            urlBuilder.append("&p_itemcategorycode=").append(p_itemcategorycode);
            urlBuilder.append("&p_itemcode=").append(p_itemcode);
            urlBuilder.append("&p_kindcode=").append(p_kindcode);
            urlBuilder.append("&p_productrankcode=").append(p_productrankcode);
            urlBuilder.append("&p_countrycode=").append(p_countrycode);
            urlBuilder.append("&p_convert_kg_yn=Y");
            urlBuilder.append("&p_cert_key=").append(serviceKey);
            urlBuilder.append("&p_cert_id=").append(serviceId);
            urlBuilder.append("&p_returntype=json");

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            InputStreamReader isr = new InputStreamReader(
                    (responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream(),
                    "UTF-8"
            );

            BufferedReader rd = new BufferedReader(isr);
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();
            conn.disconnect();

            // 실제 응답이 JSON인지 확인
            System.out.println("응답: " + response.toString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.toString());

            return root;

        } catch (Exception e) {
            e.printStackTrace(); // 로그에 예외 출력
            throw new RuntimeException("도매 시세 데이터를 가져오는 데 실패했습니다.", e);
        }
    }


}