package agridata.spring.controller;

import agridata.spring.dto.request.UserRequestDTO;
import agridata.spring.dto.response.UserResponseDTO;
import agridata.spring.global.ApiResponse;
import agridata.spring.service.UserCommandService;
import agridata.spring.service.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

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

    // 사용자 관심품목 조회 API
    @Operation(summary = "사용자 관심품목 조회 API", description = "사용자 관심품목 조회 API입니다.")
    @GetMapping("/prefer-item")
    public ApiResponse<String> getPreferItem(){
        var preferItem = userQueryService.getUserPreferItem();
        return ApiResponse.onSuccess(preferItem);
    }

    // 사용자 지역 조회 API
    @Operation(summary = "사용자 지역 조회 API", description = "사용자 지역 조회 API입니다.")
    @GetMapping("/region")
    public ApiResponse<String> getRegion() {
        var region = userQueryService.getUserRegion();
        return ApiResponse.onSuccess(region);
    }




}
