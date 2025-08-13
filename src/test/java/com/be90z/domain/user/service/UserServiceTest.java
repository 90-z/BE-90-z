package com.be90z.domain.user.service;

import com.be90z.domain.user.dto.request.UserMypageUpdateReqDTO;
import com.be90z.domain.user.dto.response.UserMypageResDTO;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.entity.UserAuthority;
import com.be90z.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("마이페이지 정보 조회 성공")
    void getMypage_Success() {
        // given
        Long userId = 1L;
        User user = User.builder()
                .userId(userId)
                .provider("google")
                .nickname("테스트유저")
                .email("test@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        UserMypageResDTO result = userService.getMypage(userId);

        // then
        assertThat(result.getUserName()).isEqualTo("테스트유저");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("마이페이지 정보 조회 실패 - 사용자를 찾을 수 없음")
    void getMypage_Fail_UserNotFound() {
        // given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getMypage(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("마이페이지 정보 수정 성공")
    void updateMypage_Success() {
        // given
        Long userId = 1L;
        UserMypageUpdateReqDTO request = new UserMypageUpdateReqDTO("수정된유저명");
        User existingUser = User.builder()
                .userId(userId)
                .provider("google")
                .nickname("기존유저명")
                .email("test@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        UserMypageResDTO result = userService.updateMypage(userId, request);

        // then
        assertThat(result.getUserName()).isEqualTo("수정된유저명");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("마이페이지 정보 수정 실패 - 사용자를 찾을 수 없음")
    void updateMypage_Fail_UserNotFound() {
        // given
        Long userId = 999L;
        UserMypageUpdateReqDTO request = new UserMypageUpdateReqDTO("수정된유저명");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateMypage(userId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }
}