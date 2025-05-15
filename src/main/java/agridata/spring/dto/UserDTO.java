package agridata.spring.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class UserDTO {

    @Getter
    public static class UserRequestDTO {

    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserResponseDTO {

    }
}
