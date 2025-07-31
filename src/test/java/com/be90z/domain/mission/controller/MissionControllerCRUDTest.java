package com.be90z.domain.mission.controller;

import com.be90z.domain.mission.dto.request.MissionCreateReqDTO;
import com.be90z.domain.mission.dto.request.MissionJoinReqDTO;
import com.be90z.domain.mission.dto.request.MissionUpdateReqDTO;
import com.be90z.domain.mission.dto.response.MissionCreateResDTO;
import com.be90z.domain.mission.dto.response.MissionDetailResDTO;
import com.be90z.domain.mission.dto.response.MissionJoinResDTO;
import com.be90z.domain.mission.entity.Mission;
import com.be90z.domain.mission.entity.MissionStatus;
import com.be90z.domain.mission.repository.MissionRepository;
import com.be90z.domain.mission.service.MissionService;
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
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@DisplayName("MissionController CRUD 테스트")
class MissionControllerCRUDTest {


    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    private User testUser;
    private Mission testMission;

    @BeforeEach
    void setUp() {
        // MockMvc는 @AutoConfigureMockMvc로 자동 설정됨

        // 테스트 사용자 생성
        testUser = User.builder()
                .provider("kakao")
                .nickname("테스트유저")
                .email("test@example.com")
                .auth(UserAuthority.USER)
                .gender(Gender.MAN)
                .birth(1990)
                .createdAt(LocalDateTime.now())
                .build();
        testUser = userRepository.save(testUser);

        // 테스트 미션 생성
        testMission = Mission.builder()
                .missionName("테스트 미션")
                .missionContent("테스트 미션 내용")
                .missionStatus(MissionStatus.ACTIVE)
                .missionMax(100)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(6))
                .createdAt(LocalDateTime.now())
                .build();
        testMission = missionRepository.save(testMission);
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/v1/mission - 미션 생성 성공")
    void createMission_Success() throws Exception {
        // given
        MissionCreateReqDTO request = MissionCreateReqDTO.builder()
                .missionName("새로운 미션")
                .missionContent("새로운 미션 내용")
                .missionMax(50)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(7))
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/mission")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.missionName").value("새로운 미션"))
            .andExpect(jsonPath("$.missionContent").value("새로운 미션 내용"))
            .andExpect(jsonPath("$.missionMax").value(50));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/v1/mission - 잘못된 요청으로 미션 생성 실패")
    void createMission_BadRequest() throws Exception {
        // given
        MissionCreateReqDTO request = MissionCreateReqDTO.builder()
                // missionName 누락
                .missionContent("미션 내용")
                .missionMax(50)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(7))
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/mission")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/v1/mission/{missionCode} - 미션 수정 성공")
    void updateMission_Success() throws Exception {
        // given
        MissionUpdateReqDTO request = MissionUpdateReqDTO.builder()
                .missionName("수정된 미션")
                .missionContent("수정된 미션 내용")
                .missionMax(200)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .build();

        // when & then
        mockMvc.perform(put("/api/v1/mission/{missionCode}", testMission.getMissionCode())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.missionName").value("수정된 미션"))
            .andExpect(jsonPath("$.missionContent").value("수정된 미션 내용"))
            .andExpect(jsonPath("$.missionMax").value(200));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/v1/mission/{missionCode} - 존재하지 않는 미션 수정 실패")
    void updateMission_NotFound() throws Exception {
        // given
        MissionUpdateReqDTO request = MissionUpdateReqDTO.builder()
                .missionName("수정된 미션")
                .missionContent("수정된 미션 내용")
                .missionMax(200)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .build();

        // when & then
        mockMvc.perform(put("/api/v1/mission/{missionCode}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/v1/mission/{missionCode} - 미션 삭제 성공")
    void deleteMission_Success() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/mission/{missionCode}", testMission.getMissionCode()))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/v1/mission/{missionCode} - 존재하지 않는 미션 삭제 실패")
    void deleteMission_NotFound() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/mission/{missionCode}", 999L))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/v1/mission/join - 미션 참여 성공")
    void joinMission_Success() throws Exception {
        // given
        MissionJoinReqDTO request = MissionJoinReqDTO.builder()
                .userId(testUser.getUserId())
                .missionCode(testMission.getMissionCode())
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/mission/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.participateCode").exists())
            .andExpect(jsonPath("$.message").value("미션 참여가 완료되었습니다."))
            .andExpect(jsonPath("$.participateStatus").value("PART_BEFORE"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/v1/mission/join - 존재하지 않는 사용자로 미션 참여 실패")
    void joinMission_UserNotFound() throws Exception {
        // given
        MissionJoinReqDTO request = MissionJoinReqDTO.builder()
                .userId(999L)
                .missionCode(testMission.getMissionCode())
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/mission/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/v1/mission/status - 미션 상태 변경 성공")
    void updateMissionStatus_Success() throws Exception {
        // given - 먼저 미션에 참여
        MissionJoinReqDTO joinRequest = MissionJoinReqDTO.builder()
                .userId(testUser.getUserId())
                .missionCode(testMission.getMissionCode())
                .build();
        
        mockMvc.perform(post("/api/v1/mission/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(joinRequest)));

        // when & then - 상태 변경
        mockMvc.perform(patch("/api/v1/mission/status")
                .param("userId", String.valueOf(testUser.getUserId()))
                .param("missionCode", String.valueOf(testMission.getMissionCode()))
                .param("status", "PART_COMPLETE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PART_COMPLETE"));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/v1/mission/status - 존재하지 않는 참여로 상태 변경 실패")
    void updateMissionStatus_NotFound() throws Exception {
        // when & then
        mockMvc.perform(patch("/api/v1/mission/status")
                .param("userId", String.valueOf(testUser.getUserId()))
                .param("missionCode", String.valueOf(testMission.getMissionCode()))
                .param("status", "PART_COMPLETE"))
            .andExpect(status().isNotFound());
    }
}