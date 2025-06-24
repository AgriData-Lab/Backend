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
import agridata.spring.service.WholesalePriceApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl {

    private final WholesalePriceApiService priceApiService;
    private final NotificationRepository notificationRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final UserRepository userRepository;
    private final ItemCsvMapper itemCsvMapper;

    public void checkAndLogPriceAlerts() {
        List<Notification> notifications = notificationRepository.findAllByIsActiveTrue();

        for (Notification n : notifications) {
            ItemCsvMapper.ItemCode code = itemCsvMapper.getCode(n.getItemName());
            if (code == null) continue;

            String responseXml = priceApiService.getPriceData(
                    code.getItemCode(), null, code.getItemCategoryCode(), null,
                    String.valueOf(n.getUser().getRegion()), getToday(), getToday()
            );

            int currentPrice = parsePrice(responseXml);
            if (currentPrice >= n.getTargetPrice()) {
                NotificationLog log = NotificationLog.builder()
                        .notification(n)
                        .currentPrice(currentPrice)
                        .triggeredAt(LocalDateTime.now())
                        .message("설정한 가격 이상 도달: " + currentPrice)
                        //.field(n.getKind().getKindName())
                        .type(n.getType().name())
                        .build();
                notificationLogRepository.save(log);
            }
        }
    }

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
