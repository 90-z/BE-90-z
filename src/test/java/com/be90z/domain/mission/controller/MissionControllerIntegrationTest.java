package com.be90z.domain.mission.controller;

import com.be90z.domain.mission.entity.Mission;
import com.be90z.domain.mission.entity.MissionStatus;
import com.be90z.domain.mission.repository.MissionRepository;
import com.be90z.domain.user.entity.Gender;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.entity.UserAuthority;
import com.be90z.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("MissionController 통합 테스트")
class MissionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MissionRepository missionRepository;

    private User testUser;
    private Mission testMission;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .provider("kakao")
                .nickname("통합테스트유저")
                .email("integration@example.com")
                .auth(UserAuthority.USER)
                .gender(Gender.MAN)
                .birth(1990)
                .createdAt(LocalDateTime.now())
                .build();
        testUser = userRepository.save(testUser);

        testMission = Mission.builder()
                .missionName("매일 물 8잔 마시기")
                .missionContent("하루에 물 8잔을 마시고 인증샷을 올려주세요")
                .missionStatus(MissionStatus.ACTIVE)
                .missionMax(100)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(6))
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
        testMission = missionRepository.save(testMission);
    }

    @Test
    @WithMockUser
    @DisplayName("실제 DB 연동 - 활성 미션 목록 조회 성공")
    void getActiveMissions_WithRealDB_ReturnsActiveMissions() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/mission")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].missionName").value("매일 물 8잔 마시기"))
                .andExpect(jsonPath("$[0].missionStatus").value("ACTIVE"));
    }

    @Test
    @WithMockUser
    @DisplayName("실제 DB 연동 - 미션 상세 조회 성공")
    void getMissionDetail_WithRealDB_ReturnsMissionDetail() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/mission/{missionCode}", testMission.getMissionCode())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.missionName").value("매일 물 8잔 마시기"))
                .andExpect(jsonPath("$.missionContent").value("하루에 물 8잔을 마시고 인증샷을 올려주세요"))
                .andExpect(jsonPath("$.missionStatus").value("ACTIVE"));
    }

    @Test
    @WithMockUser
    @DisplayName("실제 DB 연동 - 존재하지 않는 미션 조회 시 404 에러")
    void getMissionDetail_WithRealDB_NotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/mission/{missionCode}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}