package agridata.spring.service.impl;

import agridata.spring.domain.Notification;
import agridata.spring.domain.NotificationLog;
import agridata.spring.domain.User;
import agridata.spring.domain.enums.Type;
import agridata.spring.dto.ItemCsvMapper;
import agridata.spring.dto.request.NotificationRequestDTO;
import agridata.spring.repository.NotificationLogRepository;
import agridata.spring.repository.NotificationRepository;
import agridata.spring.repository.UserRepository;
import agridata.spring.service.RetailPriceApiService;
import agridata.spring.service.WholesalePriceApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Service
public class NotificationServiceImpl {

    private final WholesalePriceApiService wholsalePriceApiService;
    private final RetailPriceApiService retailPriceApiService;
    private final NotificationRepository notificationRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final UserRepository userRepository;
    private final ItemCsvMapper itemCsvMapper;

    @Transactional
    public void checkAndLogPriceAlerts() {
        List<Notification> notifications = notificationRepository.findAllByIsActiveTrue();
        log.info("🔔 알림 확인 시작: 총 {}건", notifications.size());

        for (Notification n : notifications) {
            ItemCsvMapper.ItemCode code = itemCsvMapper.getCode(n.getItemName());
            if (code == null) {
                log.warn("❌ 매핑 실패: itemName = {}", n.getItemName());
                continue;
            }

            String responseXml;
            if (n.getType() == Type.WHOLESALE) {
                responseXml = wholsalePriceApiService.getPriceData(
                        code.getItemCode(), null, code.getItemCategoryCode(), null, null,
                        getToday(), getToday()
                );
            } else {
                String countyCode =  n.getCountyCode();  // RETAIL은 지역 필수

                responseXml = retailPriceApiService.getPriceData(
                        code.getItemCode(), null, code.getItemCategoryCode(), null,
                        countyCode, getToday(), getToday()
                );

            }

            log.info("📥 응답 XML ({}): {}", n.getItemName(), responseXml);

            // XML 파싱
            Document doc = Jsoup.parse(responseXml, "", org.jsoup.parser.Parser.xmlParser());
            List<Element> items = doc.select("data > item");



            for (Element item : items) {
                // 평균, 평년 제거
                Element countyElem = item.selectFirst("countyname");
                String county = countyElem != null ? countyElem.text() : "";
                if (county.equals("평년") || county.equals("평균") || county.isBlank()) {
                    continue;
                }
                String priceText = item.selectFirst("price") != null ? item.selectFirst("price").text().replaceAll(",", "") : "";

                String itemName = item.selectFirst("itemname") != null
                        ? item.selectFirst("itemname").text()
                        : "알 수 없음"; // 또는 n.getItemName()

                // price가 사용자가 설정한 가격(getTargetPrice)보다 높아지면 도매, 낮아지면 소매
                try {
                    int price = Integer.parseInt(priceText);

                    boolean shouldNotify =
                            (n.getType() == Type.WHOLESALE && price > n.getTargetPrice()) ||
                                    (n.getType() == Type.RETAIL && price < n.getTargetPrice());
                    if (shouldNotify) {
                        String direction = (n.getType() == Type.WHOLESALE) ? "상승" : "하락";
                        NotificationLog logEntity = NotificationLog.builder()
                                .field(itemName)
                                .notification(n)
                                .currentPrice(price)
                                .triggeredAt(LocalDateTime.now())
                                .message("가격 " + direction + " 감지 (" + county + "): " + price + "원")
                                .type(n.getType().name())
                                .build();

                        notificationLogRepository.save(logEntity);
                        log.info("✅ 알림 저장 완료: {} (지역: {})", logEntity, county);
                    } else {
                        log.info("⏹ 조건 미충족 - 지역: {}, 현재가: {}, 기준가: {}", county, price, n.getTargetPrice());
                    }

                } catch (NumberFormatException e) {
                    log.warn("⚠ 가격 파싱 오류: '{}' (지역: {})", priceText, county);
                }

            }
        }
    }



    @Transactional
    public void createNotification(Long userId, NotificationRequestDTO.CreateRequest dto) {
        User user = userRepository.findById(userId).orElseThrow();

        ItemCsvMapper.ItemCode code = itemCsvMapper.getCode(dto.getItemName());
        if (code == null) throw new IllegalArgumentException("잘못된 품목명입니다.");

        // 한글 type을 enum으로 변환
        Type typeEnum;
        switch (dto.getType()) {
            case "도매" -> typeEnum = Type.WHOLESALE;
            case "소매" -> typeEnum = Type.RETAIL;
            default -> throw new IllegalArgumentException("유효하지 않은 type 값입니다: " + dto.getType());
        }

        Notification notification = Notification.builder()
                .user(user)
                .itemName(dto.getItemName())
                .type(typeEnum) // 💡 변환된 enum 사용
                .targetPrice(dto.getTargetPrice())
                .isActive(dto.getIsActive())
                .countyCode(dto.getCountyCode())  // 🆕 지역 추가
                .build();

        notificationRepository.save(notification);
    }

    private int parsePrice(String xml) {
        Document doc = Jsoup.parse(xml, "", org.jsoup.parser.Parser.xmlParser());
        Element item = doc.selectFirst("item");
        if (item != null) {
            String priceText = item.selectFirst("price") != null ? item.selectFirst("price").text().replaceAll(",", "") : "0";
            try {
                return Integer.parseInt(priceText);
            } catch (NumberFormatException e) {
                log.warn("가격 파싱 오류: {}", priceText);
            }
        }
        return 0;
    }

    private String getToday() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
