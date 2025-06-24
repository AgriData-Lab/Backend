package agridata.spring.controller;

import agridata.spring.config.SecurityUtil;
import agridata.spring.domain.NotificationLog;
import agridata.spring.dto.request.NotificationRequestDTO;
import agridata.spring.dto.response.NotificationLogDTO;
import agridata.spring.global.ApiResponse;
import agridata.spring.global.error.status.ErrorStatus;
import agridata.spring.repository.NotificationLogRepository;
import agridata.spring.service.impl.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationLogRepository notificationLogRepository;
    private final NotificationServiceImpl notificationServiceImpl;
    private  SecurityUtil securityUtil;

    @PostMapping("/notifications")
    public ApiResponse<NotificationRequestDTO.CreateRequest> createNotification(@RequestBody NotificationRequestDTO.CreateRequest dto) {
        if (dto.getItemName() == null || dto.getItemName().trim().isEmpty()) {
            return ApiResponse.onFailure(ErrorStatus.ITEM_NAME_REQUIRED.getCode(), ErrorStatus.ITEM_NAME_REQUIRED.getMessage(), dto);
        }

        Long userId = securityUtil.getCurrentMemberId();
        notificationServiceImpl.createNotification(userId, dto);
        return ApiResponse.onSuccess(null);
    }



    @GetMapping
    public ApiResponse<List<NotificationLogDTO>> getUserNotifications() {
        Long userId = securityUtil.getCurrentMemberId();
        List<NotificationLog> logs = notificationLogRepository
                .findByNotification_User_UserIdOrderByTriggeredAtDesc(userId);

        List<NotificationLogDTO> result = logs.stream()
                .map(NotificationLogDTO::from)
                .toList();

        return ApiResponse.onSuccess(result);
    }

}
