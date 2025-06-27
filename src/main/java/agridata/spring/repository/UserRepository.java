package agridata.spring.repository;

import agridata.spring.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // id로 사용자 조회
    Optional<User> findById(Long id);

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 닉네임 중복 확인
    boolean existsByNickname(String nickname);

    // 이메일 중복 확인
    boolean existsByEmail(String email);
}
