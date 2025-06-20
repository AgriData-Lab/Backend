package agridata.spring.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    // 회원가입 요청 DTO
    public static class SignupDTO {
        private String name;

        @JsonProperty("email")
        private String id_email;

        private String password;
        private String region;
        private String interestItem;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginDTO {
        @JsonProperty("email")
        private String id_email;

        private String password;
    }


}
