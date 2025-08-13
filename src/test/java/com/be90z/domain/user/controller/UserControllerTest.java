package com.be90z.domain.user.controller;

import com.be90z.domain.user.dto.request.UserMypageUpdateReqDTO;
import com.be90z.domain.user.dto.response.UserMypageResDTO;
import com.be90z.domain.user.service.UserService;
import com.be90z.global.util.AuthUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthUtil authUtil;

    @Test
    @DisplayName("마이페이지 정보 조회 성공")
    @WithMockUser
    void getMypage_Success() throws Exception {
        // given
        String token = "Bearer valid-jwt-token";
        Long userId = 1L;
        UserMypageResDTO expectedResponse = UserMypageResDTO.builder()
                .userName("테스트유저")
                .email("test@example.com")
                .build();

        when(authUtil.getUserIdFromToken(token)).thenReturn(userId);
        when(userService.getMypage(userId)).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(get("/api/v1/user/mypage")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("테스트유저"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("마이페이지 정보 조회 실패 - 토큰 없음")
    void getMypage_Fail_NoToken() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/user/mypage"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("마이페이지 정보 조회 실패 - 유효하지 않은 토큰")
    void getMypage_Fail_InvalidToken() throws Exception {
        // given
        String invalidToken = "Bearer invalid-token";
        when(authUtil.getUserIdFromToken(invalidToken)).thenReturn(null);

        // when & then
        mockMvc.perform(get("/api/v1/user/mypage")
                        .header("Authorization", invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("마이페이지 정보 수정 성공")
    @WithMockUser
    void updateMypage_Success() throws Exception {
        // given
        String token = "Bearer valid-jwt-token";
        Long userId = 1L;
        UserMypageUpdateReqDTO request = new UserMypageUpdateReqDTO("수정된유저명");
        UserMypageResDTO expectedResponse = UserMypageResDTO.builder()
                .userName("수정된유저명")
                .email("test@example.com")
                .build();

        when(authUtil.getUserIdFromToken(token)).thenReturn(userId);
        when(userService.updateMypage(eq(userId), any(UserMypageUpdateReqDTO.class)))
                .thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(post("/api/v1/user/mypage")
                        .with(csrf())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("수정된유저명"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("마이페이지 정보 수정 실패 - 토큰 없음")
    @WithMockUser
    void updateMypage_Fail_NoToken() throws Exception {
        // given
        UserMypageUpdateReqDTO request = new UserMypageUpdateReqDTO("수정된유저명");

        // when & then
        mockMvc.perform(post("/api/v1/user/mypage")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}