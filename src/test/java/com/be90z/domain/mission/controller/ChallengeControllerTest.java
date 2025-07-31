package com.be90z.domain.mission.controller;

import com.be90z.domain.mission.entity.Mission;
import com.be90z.domain.mission.entity.MissionParticipation;
import com.be90z.domain.mission.entity.MissionStatus;
import com.be90z.domain.mission.entity.ParticipateStatus;
import com.be90z.domain.mission.repository.MissionParticipationRepository;
import com.be90z.domain.mission.repository.MissionRepository;
import com.be90z.domain.user.entity.Gender;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.entity.UserAuthority;
import com.be90z.domain.user.repository.UserRepository;
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
@DisplayName("ChallengeController 테스트")
class ChallengeControllerTest {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionParticipationRepository participationRepository;

    @Autowired
    private MockMvc mockMvc;
    private User testUser;
    private Mission testMission1;
    private Mission testMission2;

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

        // 테스트 미션들 생성
        testMission1 = Mission.builder()
                .missionName("진행 중 미션")
                .missionContent("진행 중인 미션 내용")
                .missionStatus(MissionStatus.ACTIVE)
                .missionMax(100)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(6))
                .createdAt(LocalDateTime.now())
                .build();
        testMission1 = missionRepository.save(testMission1);

        testMission2 = Mission.builder()
                .missionName("완료된 미션")
                .missionContent("완료된 미션 내용")
                .missionStatus(MissionStatus.ACTIVE)
                .missionMax(50)
                .startDate(LocalDateTime.now().minusDays(5))
                .endDate(LocalDateTime.now().plusDays(2))
                .createdAt(LocalDateTime.now())
                .build();
        testMission2 = missionRepository.save(testMission2);

        // 미션 참여 데이터 생성
        MissionParticipation participation1 = MissionParticipation.builder()
                .participateStatus(ParticipateStatus.PART_BEFORE)
                .user(testUser)
                .mission(testMission1)
                .build();
        participationRepository.save(participation1);

        MissionParticipation participation2 = MissionParticipation.builder()
                .participateStatus(ParticipateStatus.PART_COMPLETE)
                .user(testUser)
                .mission(testMission2)
                .build();
        participationRepository.save(participation2);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/v1/challenge/status/{userId} - 사용자 챌린지 현황 조회 성공")
    void getChallengeStatus_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/challenge/status/{userId}", testUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(testUser.getUserId()))
            .andExpect(jsonPath("$.nickname").value("테스트유저"))
            .andExpect(jsonPath("$.participatingMissions").value(1))
            .andExpect(jsonPath("$.completedMissions").value(1))
            .andExpect(jsonPath("$.raffleEntries").value(1))
            .andExpect(jsonPath("$.missionList").isArray())
            .andExpect(jsonPath("$.missionList[0].missionName").exists())
            .andExpect(jsonPath("$.missionList[0].participateStatus").exists())
            .andExpect(jsonPath("$.winnerHistory").isArray());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/v1/challenge/status/{userId} - 존재하지 않는 사용자로 조회 실패")
    void getChallengeStatus_UserNotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/challenge/status/{userId}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/v1/challenge/status/{userId} - 미션 참여 내역이 없는 사용자 조회 성공")
    void getChallengeStatus_NoParticipations() throws Exception {
        // given - 새로운 사용자 생성 (미션 참여 내역 없음)
        User newUser = User.builder()
                .provider("kakao")
                .nickname("새로운유저")
                .email("new@example.com")
                .auth(UserAuthority.USER)
                .gender(Gender.WOMAN)
                .birth(1995)
                .createdAt(LocalDateTime.now())
                .build();
        newUser = userRepository.save(newUser);

        // when & then
        mockMvc.perform(get("/api/v1/challenge/status/{userId}", newUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(newUser.getUserId()))
            .andExpect(jsonPath("$.nickname").value("새로운유저"))
            .andExpect(jsonPath("$.participatingMissions").value(0))
            .andExpect(jsonPath("$.completedMissions").value(0))
            .andExpect(jsonPath("$.raffleEntries").value(0))
            .andExpect(jsonPath("$.missionList").isArray())
            .andExpect(jsonPath("$.missionList").isEmpty())
            .andExpect(jsonPath("$.winnerHistory").isArray());
    }
}