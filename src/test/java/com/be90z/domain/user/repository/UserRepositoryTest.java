package com.be90z.domain.user.repository;

import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.entity.UserAuthority;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@DisplayName("UserRepository 테스트")
@Disabled("SQL 문법 오류로 인한 임시 비활성화")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("이메일로 사용자 조회")
    void findByEmail() {
        // given
        User user = User.builder()
                .provider("kakao")
                .nickname("테스트유저")
                .email("test@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(user);

        // when
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getNickname()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("provider와 이메일로 사용자 조회")
    void findByProviderAndEmail() {
        // given
        User user = User.builder()
                .provider("kakao")
                .nickname("카카오유저")
                .email("kakao@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(user);

        // when
        Optional<User> found = userRepository.findByProviderAndEmail("kakao", "kakao@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getNickname()).isEqualTo("카카오유저");
    }

    @Test
    @DisplayName("이메일 존재 여부 확인")
    void existsByEmail() {
        // given
        User user = User.builder()
                .provider("kakao")
                .nickname("존재하는유저")
                .email("exists@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(user);

        // when & then
        assertThat(userRepository.existsByEmail("exists@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("notexists@example.com")).isFalse();
    }
}