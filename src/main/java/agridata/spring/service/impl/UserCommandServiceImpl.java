package agridata.spring.service.impl;

import agridata.spring.domain.User;
import agridata.spring.domain.enums.Region;
import agridata.spring.dto.request.UserRequestDTO;
import agridata.spring.dto.response.UserResponseDTO;
import agridata.spring.repository.UserRepository;
import agridata.spring.security.TokenProvider;
import agridata.spring.service.UserCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {
    
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 회원가입
    @Override
    public UserResponseDTO.SignupDTO create(UserRequestDTO.SignupDTO dto) {
        User user = User.builder()
                .nickname(dto.getName())
                .email(dto.getId_email())
                .password(passwordEncoder.encode(dto.getPassword()))
                .region(Region.valueOf(dto.getRegion()))
                .interestItem(dto.getInterestItem())
                .build();
        User result = userRepository.save(user);

        return UserResponseDTO.SignupDTO.builder().id(result.getUserId()).build();
    }

    // 로그인
    @Override
    public UserResponseDTO.LoginDTO login(UserRequestDTO.LoginDTO dto) {
        // Optional 사용하는 이유 == "값이 있을 수도, 없을 수도 있다"상황 명확히 표현 (null 체크 강제)
        // Optional = null이 될 수 있는 값을 감싸는 Wrapper(포장) 클래스
        final Optional<User> user = userRepository.findByEmail(dto.getId_email());
        if(user.isPresent() && passwordEncoder.matches(dto.getPassword(), user.get().getPassword())){
            return UserResponseDTO.LoginDTO.builder().token(tokenProvider.create(user.get()))
                    .id(user.get().getUserId()).build();
        } else throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");

    }

}
