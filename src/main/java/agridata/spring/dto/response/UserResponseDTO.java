package agridata.spring.dto.response;

import lombok.*;

public class UserResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupDTO{
        Long id; // 사용자 id값
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginDTO{
        String token;
        Long id;
    }
}
