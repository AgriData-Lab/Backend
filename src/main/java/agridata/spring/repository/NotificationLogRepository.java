package agridata.spring.repository;

import agridata.spring.domain.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
//    List<NotificationLog> findByNotification_User_UserIdOrderByTriggeredAtDesc(Long userId);
    // 기존 함수에 시작 시간, 마감 시간 추가
    List<NotificationLog> findByNotification_User_UserIdAndTriggeredAtBetweenOrderByTriggeredAtDesc(
            Long userId, LocalDateTime start, LocalDateTime end);

    // NotificationLogRepository.java
    boolean existsByTriggeredAtBetween(LocalDateTime start, LocalDateTime end);
}