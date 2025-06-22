package agridata.spring.config;

import agridata.spring.domain.User;
import agridata.spring.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

    private static ApplicationContext applicationContext;

    // ApplicationContext를 외부에서 주입
    public static void setApplicationContext(ApplicationContext context) {
        SecurityUtil.applicationContext = context;
    }

    public static Long getCurrentMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
        }

        try {
            String userEmail = authentication.getName();  // 토큰에서 가져온 이메일
            UserRepository repository = applicationContext.getBean(UserRepository.class);
            User user = repository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("해당 이메일의 사용자를 찾을 수 없습니다."));
            return user.getUserId();
        } catch (Exception e) {
            throw new RuntimeException("유저 식별 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
