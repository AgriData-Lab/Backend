package agridata.spring.service;

import agridata.spring.service.impl.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationServiceImpl notificationServiceImpl;
    // 주기적으로 실행할 작업을 정의할 때 사용
    @Scheduled(cron = "0 0 0 * * *") // 매 시간 정각마다

    public void runNotificationJob() {
        notificationServiceImpl.checkAndLogPriceAlerts();
    }
}
