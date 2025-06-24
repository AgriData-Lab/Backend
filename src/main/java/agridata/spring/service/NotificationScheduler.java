package agridata.spring.service;

import agridata.spring.service.impl.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationServiceImpl notificationServiceImpl;

    @Scheduled(cron = "0 0 * * * *") // 매 시간 정각마다
    public void runNotificationJob() {
        notificationServiceImpl.checkAndLogPriceAlerts();
    }
}
