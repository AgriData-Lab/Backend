package agridata.spring.service;

import agridata.spring.dto.request.UserRequestDTO;
import agridata.spring.dto.response.UserResponseDTO;

public interface UserCommandService {
    UserResponseDTO.SignupDTO create(UserRequestDTO.SignupDTO dto);

    public UserResponseDTO.LoginDTO login(final UserRequestDTO.LoginDTO dto);

}
