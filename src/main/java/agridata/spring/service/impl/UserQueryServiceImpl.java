package agridata.spring.service.impl;

import agridata.spring.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {
}
