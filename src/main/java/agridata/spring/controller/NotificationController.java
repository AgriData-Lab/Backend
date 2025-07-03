package agridata.spring.controller;

import agridata.spring.config.SecurityUtil;
import agridata.spring.domain.NotificationLog;
import agridata.spring.dto.LocationCodeLoader;
import agridata.spring.dto.request.NotificationRequestDTO;
import agridata.spring.dto.response.NotificationLogDTO;
import agridata.spring.global.ApiResponse;
import agridata.spring.global.error.status.ErrorStatus;
import agridata.spring.repository.NotificationLogRepository;
import agridata.spring.service.impl.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationLogRepository notificationLogRepository;
    private final NotificationServiceImpl notificationServiceImpl;
    private  SecurityUtil securityUtil;
    private final LocationCodeLoader locationCodeLoader;

    @PostMapping("/notifications")
    public ApiResponse<NotificationRequestDTO.CreateRequest> createNotification(@RequestBody NotificationRequestDTO.CreateRequest dto) {
        if (dto.getItemName() == null || dto.getItemName().trim().isEmpty()) {
            return ApiResponse.onFailure(ErrorStatus.ITEM_NAME_REQUIRED.getCode(), ErrorStatus.ITEM_NAME_REQUIRED.getMessage(), dto);
        }

        Long userId = securityUtil.getCurrentMemberId();
        notificationServiceImpl.createNotification(userId, dto);
        log.info("📥 알림 생성 요청: {}", dto); // ✅ 여기에 전체 DTO 로그 찍기
        return ApiResponse.onSuccess(null);
    }



    @GetMapping
    public ApiResponse<List<NotificationLogDTO>> getUserNotifications() {
        Long userId = securityUtil.getCurrentMemberId();

        // 17시 기준으로 오늘 또는 어제 알림 범위 설정
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start, end;

        if (now.getHour() >= 17) {
            start = now.withHour(17).withMinute(0).withSecond(0).withNano(0);
            end = start.plusDays(1).withHour(16).withMinute(59).withSecond(59);
        } else {
            start = now.minusDays(1).withHour(17).withMinute(0).withSecond(0).withNano(0);
            end = now.withHour(16).withMinute(59).withSecond(59);
        }

        List<NotificationLog> logs = notificationLogRepository
                .findByNotification_User_UserIdAndTriggeredAtBetweenOrderByTriggeredAtDesc(userId, start, end);

        List<NotificationLogDTO> result = logs.stream()
                .map(log -> NotificationLogDTO.from(log, locationCodeLoader))  // 💡 지역명 포함 변환
                .toList();

        return ApiResponse.onSuccess(result);
    }

}
