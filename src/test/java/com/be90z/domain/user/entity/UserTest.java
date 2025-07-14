package com.be90z.domain.user.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

@DisplayName("User 엔티티 테스트")
class UserTest {

    @Test
    @DisplayName("User 엔티티 생성 테스트")
    void createUser() {
        // given
        String provider = "kakao";
        String nickname = "테스트유저";
        String email = "test@example.com";
        UserAuthority auth = UserAuthority.USER;
        LocalDateTime createdAt = LocalDateTime.now();
        Gender gender = Gender.WOMAN;
        int birth = 1990;

        // when
        User user = User.builder()
                .provider(provider)
                .nickname(nickname)
                .email(email)
                .auth(auth)
                .createdAt(createdAt)
                .gender(gender)
                .birth(birth)
                .build();

        // then
        assertThat(user.getProvider()).isEqualTo(provider);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getAuth()).isEqualTo(auth);
        assertThat(user.getCreatedAt()).isEqualTo(createdAt);
        assertThat(user.getGender()).isEqualTo(gender);
        assertThat(user.getBirth()).isEqualTo(birth);
    }

    @Test
    @DisplayName("User 엔티티 필수 필드 검증")
    void validateRequiredFields() {
        // given & when & then
        assertThatThrownBy(() -> {
            User.builder()
                    .nickname("테스트유저")
                    .email("test@example.com")
                    // provider 누락
                    .build();
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Provider cannot be null");
    }
}