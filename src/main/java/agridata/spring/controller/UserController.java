package agridata.spring.controller;

import agridata.spring.dto.request.UserRequestDTO;
import agridata.spring.dto.response.UserResponseDTO;
import agridata.spring.global.ApiResponse;
import agridata.spring.service.UserCommandService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserCommandService userCommandService;

    // 회원가입 API
    @Operation(summary = "회원가입 API", description = "회원가입 API입니다.")
    @PostMapping("/auth/signup")
    public ApiResponse<UserResponseDTO.SignupDTO> registerUser(@RequestBody UserRequestDTO.SignupDTO user){
        UserResponseDTO.SignupDTO result = userCommandService.create(user);
        return ApiResponse.onSuccess(result);
    }

    // 로그인 API
    @Operation(summary = "로그인 API", description = "로그인 API입니다.")
    @PostMapping("/auth/signin")
    public ApiResponse<UserResponseDTO.LoginDTO> login(@RequestBody UserRequestDTO.LoginDTO loginDTO){
        UserResponseDTO.LoginDTO result = userCommandService.login(loginDTO);
        return ApiResponse.onSuccess(result);
    }


}
