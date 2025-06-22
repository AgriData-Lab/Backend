package agridata.spring.config;

import agridata.spring.domain.User;
import agridata.spring.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

    public static Long getCurrentMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
        }

        try {
            String userEmail = authentication.getName();
            // 정적 컨텍스트에서 Bean 접근
            UserRepository userRepository = ApplicationContextProvider.getApplicationContext().getBean(UserRepository.class);
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다: " + userEmail));
            return user.getUserId();
        } catch (Exception e) {
            throw new RuntimeException("사용자 ID를 가져오는 데 실패했습니다.", e);
        }
    }
}
