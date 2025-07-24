package com.be90z.domain.user.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

@DisplayName("User 엔티티 테스트")
class UserTest {

    @Test
    @DisplayName("User 엔티티 생성 테스트 - 새 스키마")
    void createUser() {
        // given
        String provider = "kakao";
        String nickname = "테스트유저";
        String email = "test@example.com";
        UserAuthority auth = UserAuthority.USER;
        LocalDateTime createdAt = LocalDateTime.now();

        // when
        User user = User.builder()
                .provider(provider)
                .nickname(nickname)
                .email(email)
                .auth(auth)
                .createdAt(createdAt)
                .build();

        // then
        assertThat(user.getProvider()).isEqualTo(provider);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getAuth()).isEqualTo(auth);
        assertThat(user.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("User 엔티티 기본값 테스트")
    void createUserWithDefaults() {
        // given
        String provider = "kakao";
        String nickname = "테스트유저";
        String email = "test@example.com";

        // when
        User user = User.builder()
                .provider(provider)
                .nickname(nickname)
                .email(email)
                .build();

        // then
        assertThat(user.getAuth()).isEqualTo(UserAuthority.USER);
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("User 엔티티 필수 필드 검증 - provider")
    void validateProviderRequired() {
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

    @Test
    @DisplayName("User 엔티티 필수 필드 검증 - nickname")
    void validateNicknameRequired() {
        // given & when & then
        assertThatThrownBy(() -> {
            User.builder()
                    .provider("kakao")
                    .email("test@example.com")
                    // nickname 누락
                    .build();
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Nickname cannot be null");
    }

    @Test
    @DisplayName("User 엔티티 필수 필드 검증 - email")
    void validateEmailRequired() {
        // given & when & then
        assertThatThrownBy(() -> {
            User.builder()
                    .provider("kakao")
                    .nickname("테스트유저")
                    // email 누락
                    .build();
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Email cannot be null");
    }
}