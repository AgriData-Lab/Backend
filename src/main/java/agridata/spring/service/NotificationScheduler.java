package agridata.spring.service;

import agridata.spring.service.impl.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationServiceImpl notificationServiceImpl;
    // @scheduled: 주기적으로 실행할 작업을 정의할 때 사용
//    @Scheduled(cron = "0 0 0 * * *") // 매 시간 정각마다

//    @Scheduled(cron = "0 */1 * * * *") // 매 1분마다 실행

    @Scheduled(cron = "0 */5 17 * * *") // 매일 17:00 ~ 17:55 사이 5분 간격 실행




    public void runNotificationJob() {
        notificationServiceImpl.checkAndLogPriceAlerts();
    }
}
