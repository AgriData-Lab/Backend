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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImplTest.class);
    @Mock private NotificationRepository notificationRepository;
    @Mock private UserRepository userRepository;
    @Mock private ItemCsvMapper itemCsvMapper;
    @Mock private WholesalePriceApiService wholesalePriceApiService;
    @Mock private RetailPriceApiService retailPriceApiService;
    @Mock private NotificationLogRepository notificationLogRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;


    @BeforeEach
    void setUp() {
        notificationRepository = mock(NotificationRepository.class);
        userRepository = mock(UserRepository.class);
        itemCsvMapper = mock(ItemCsvMapper.class);
    }

    @Test
    void 알림생성_성공() {
        // given
        Long userId = 1L;
        NotificationRequestDTO.CreateRequest dto = NotificationRequestDTO.CreateRequest.builder()
                .itemName("쌀")
                .targetPrice(9900)
                .type("도매")
                .isActive(true)
                .build();

        User user = User.builder().userId(userId).build();
        ItemCsvMapper.ItemCode code = new ItemCsvMapper.ItemCode("100", "111", "쌀");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemCsvMapper.getCode("쌀")).thenReturn(code);

        // when
        notificationService.createNotification(userId, dto);

        // then
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(1)).save(captor.capture());

        Notification saved = captor.getValue();
        assertThat(saved.getItemName()).isEqualTo("쌀");
        assertThat(saved.getTargetPrice()).isEqualTo(9900);
        assertThat(saved.getType().name()).isEqualTo("WHOLESALE");
        assertThat(saved.getIsActive()).isTrue();
        assertThat(saved.getUser()).isEqualTo(user);
    }

    @Test
    void 알림생성_잘못된품목이면_예외() {
        // given
        Long userId = 1L;
        NotificationRequestDTO.CreateRequest dto = NotificationRequestDTO.CreateRequest.builder()
                .itemName("없는품목")
                .targetPrice(8000)
                .type("도매")
                .isActive(true)
                .build();

        User user = User.builder().userId(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemCsvMapper.getCode("없는품목")).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> notificationService.createNotification(userId, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 품목명");
    }

    @Test
    void 알림생성_잘못된타입이면_예외() {
        // given
        Long userId = 1L;
        NotificationRequestDTO.CreateRequest dto = NotificationRequestDTO.CreateRequest.builder()
                .itemName("쌀")
                .targetPrice(8000)
                .type("도소매") // 유효하지 않은 type
                .isActive(true)
                .build();

        User user = User.builder().userId(userId).build();
        ItemCsvMapper.ItemCode code = new ItemCsvMapper.ItemCode("100", "111", "쌀");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemCsvMapper.getCode("쌀")).thenReturn(code);

        // when & then
        assertThatThrownBy(() -> notificationService.createNotification(userId, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 type");
    }
    @Test
    void 가격이_올라가면_알림() {
        // given
        User user = User.builder().userId(1L).countyCode("1101").build();
        Notification notification = Notification.builder()
                .notificationId(1L)
                .user(user)
                .itemName("쌀")
                .targetPrice(10000)
                .type(Type.WHOLESALE)
                .isActive(true)
                .build();

        ItemCsvMapper.ItemCode code = new ItemCsvMapper.ItemCode("100", "111", "쌀");
        String sampleXml = """
                <response>
                    <item>
                        <price>11000</price>
                    </item>
                </response>
                """;

        when(notificationRepository.findAllByIsActiveTrue()).thenReturn(List.of(notification));
        when(itemCsvMapper.getCode("쌀")).thenReturn(code);
        when(wholesalePriceApiService.getPriceData(
                eq("111"), isNull(), eq("100"), isNull(), eq("1100"),
                eq("20250623"), eq("20250623")
        )).thenReturn(sampleXml);

        // 실제 객체에 mock 주입
        notificationService = new NotificationServiceImpl(
                wholesalePriceApiService, retailPriceApiService, notificationRepository,
                notificationLogRepository, userRepository, itemCsvMapper
        );

        // when
        notificationService.checkAndLogPriceAlerts();

        // then
        verify(notificationLogRepository, times(1)).save(any());
    }
    // 도매
    @Test
    void 가격이_도매가격_설정값보다_높으면_알림로그_저장된다() {
        // given
        User user = User.builder()
                .userId(1L)
                .countyCode("1101")  // 예: 서울의 코드값
                .build();

        Notification notification = Notification.builder()
                .notificationId(1L)
                .user(user)
                .itemName("쌀")
                .targetPrice(9000)
                .type(Type.WHOLESALE)
                .isActive(true)
                .build();

        ItemCsvMapper.ItemCode code = new ItemCsvMapper.ItemCode("100", "111", "쌀");

        String sampleXml = """
            <response>
                <item>
                    <price>10000</price>
                </item>
            </response>
            """;

        when(notificationRepository.findAllByIsActiveTrue()).thenReturn(List.of(notification));
        when(itemCsvMapper.getCode("쌀")).thenReturn(code);
        when(wholesalePriceApiService.getPriceData(
                eq("111"), isNull(), eq("100"), isNull(),
                eq("1101"), any(), any()
        )).thenReturn(sampleXml);

        // when
        notificationService = new NotificationServiceImpl(
                wholesalePriceApiService,
                retailPriceApiService,
                notificationRepository,
                notificationLogRepository,
                userRepository,
                itemCsvMapper
        );
        notificationService.checkAndLogPriceAlerts();

        // then
        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(notificationLogRepository, times(1)).save(captor.capture());

        NotificationLog savedLog = captor.getValue();

        // 로그 출력
        System.out.println("저장된 알림 로그 내용:");
        System.out.println("Message      : " + savedLog.getMessage());
        System.out.println("CurrentPrice : " + savedLog.getCurrentPrice());
        System.out.println("TriggeredAt  : " + savedLog.getTriggeredAt());
        System.out.println("Type         : " + savedLog.getType());
        System.out.println("Notification : " + (savedLog.getNotification() != null ? savedLog.getNotification().getNotificationId() : "null"));

        // 검증
        assertThat(savedLog.getCurrentPrice()).isEqualTo(10000);
        assertThat(savedLog.getType()).isEqualTo("WHOLESALE");
        assertThat(savedLog.getMessage()).contains("설정한 가격 이상 도달");
    }
    // 소매
    @Test
    void 가격이_소매가격_설정값보다_낮으면_알림로그_저장된다() {
        // given
        User user = User.builder()
                .userId(1L)
                .countyCode("1101")  // 예: 서울
                .build();

        Notification notification = Notification.builder()
                .notificationId(1L)
                .user(user)
                .itemName("양배추")
                .targetPrice(8000)  // 사용자가 설정한 목표 가격
                .type(Type.RETAIL)
                .isActive(true)
                .build();

        ItemCsvMapper.ItemCode code = new ItemCsvMapper.ItemCode("200", "222", "양배추");

        String sampleXml = """
        <response>
            <item>
                <price>7000</price>  // ✅ 설정값보다 낮음
            </item>
        </response>
        """;

        when(notificationRepository.findAllByIsActiveTrue()).thenReturn(List.of(notification));
        when(itemCsvMapper.getCode("양배추")).thenReturn(code);
        when(retailPriceApiService.getPriceData(
                eq("200"), isNull(), eq("222"), isNull(),
                eq("1101"), any(), any()
        )).thenReturn(sampleXml);

        notificationService = new NotificationServiceImpl(
                wholesalePriceApiService,
                retailPriceApiService,
                notificationRepository,
                notificationLogRepository,
                userRepository,
                itemCsvMapper
        );

        // when
        notificationService.checkAndLogPriceAlerts();

        // then
        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(notificationLogRepository, times(1)).save(captor.capture());

        NotificationLog savedLog = captor.getValue();

        // 검증
        assertThat(savedLog.getCurrentPrice()).isEqualTo(7000);
        assertThat(savedLog.getType()).isEqualTo("RETAIL");
        assertThat(savedLog.getMessage()).contains("설정한 가격 이하 도달");
    }



}