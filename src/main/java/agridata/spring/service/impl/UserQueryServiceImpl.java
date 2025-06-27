package agridata.spring.service.impl;

import agridata.spring.config.SecurityUtil;
import agridata.spring.repository.UserRepository;
import agridata.spring.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    @Override
    public String getUserPreferItem() {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return userRepository.findById(memberId).get().getInterestItem();
    }

    @Override
    public String getUserRegion() {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return userRepository.findById(memberId).get().getCountyCode();
    }

    @Override
    public boolean isDuplicate(String type, String value) {
        if ("name".equals(type)) {
            boolean exists = userRepository.existsByNickname(value);
            System.out.println("닉네임 중복 여부: " + value + " → " + exists);
            return exists;
        } else if ("email".equals(type)) {
            boolean exists = userRepository.existsByEmail(value);
            System.out.println("이메일 중복 여부: " + value + " → " + exists);
            return exists;
        } else {
            throw new IllegalArgumentException("Invalid type");
        }
    }
}
