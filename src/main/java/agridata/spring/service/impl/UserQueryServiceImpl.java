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
        return userRepository.findById(memberId).get().getRegion().name();
    }
}
