//package com.be90z.domain.challenge.controller;
//
//import com.be90z.domain.user.entity.Gender;
//import com.be90z.domain.user.entity.User;
//import com.be90z.domain.user.entity.UserAuthority;
//import com.be90z.domain.user.repository.UserRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@Transactional
//@DisplayName("ChallengeController 통합 테스트")
//class ChallengeControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private User testUser;
//
//    @BeforeEach
//    void setUp() {
//        testUser = User.builder()
//                .provider("kakao")
//                .nickname("테스트유저")
//                .email("test@example.com")
//                .auth(UserAuthority.USER)
//                .gender(Gender.MAN)
//                .birth(1990)
//                .createdAt(LocalDateTime.now())
//                .build();
//        testUser = userRepository.save(testUser);
//    }
//
//    @Test
//    @WithMockUser
//    @DisplayName("실제 DB 연동 - 챌린지 현황 조회 성공")
//    void getChallengeStatus_WithRealDB_ReturnsStatus() throws Exception {
//        // when & then
//        mockMvc.perform(get("/api/challenge/status")
//                        .param("userId", testUser.getUserId().toString())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.participatingMissions").value(0))
//                .andExpect(jsonPath("$.raffleParticipationCount").value(0))
//                .andExpect(jsonPath("$.totalRaffleParticipants").value(0))
//                .andExpect(jsonPath("$.raffleWinCount").value(0));
//    }
//
//    @Test
//    @WithMockUser
//    @DisplayName("실제 DB 연동 - 존재하지 않는 사용자로 400 에러")
//    void getChallengeStatus_WithRealDB_UserNotFound() throws Exception {
//        // when & then
//        mockMvc.perform(get("/api/challenge/status")
//                        .param("userId", "99999")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.message").value("User not found with id: 99999"))
//                .andExpect(jsonPath("$.code").value("INVALID_ARGUMENT"));
//    }
//
//    @Test
//    @WithMockUser
//    @DisplayName("실제 DB 연동 - 잘못된 파라미터 타입으로 400 에러")
//    void getChallengeStatus_WithRealDB_InvalidParameterType() throws Exception {
//        // when & then
//        mockMvc.perform(get("/api/challenge/status")
//                        .param("userId", "invalid_id")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.message").value("Invalid parameter type: userId"))
//                .andExpect(jsonPath("$.code").value("INVALID_PARAMETER_TYPE"));
//    }
//
//    @Test
//    @WithMockUser
//    @DisplayName("실제 DB 연동 - 필수 파라미터 누락으로 400 에러")
//    void getChallengeStatus_WithRealDB_MissingParameter() throws Exception {
//        // when & then
//        mockMvc.perform(get("/api/challenge/status")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.message").value("Required parameter is missing: userId"))
//                .andExpect(jsonPath("$.code").value("MISSING_PARAMETER"));
//    }
//}