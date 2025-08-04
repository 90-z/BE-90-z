package com.be90z.domain.raffle.controller;

import com.be90z.domain.raffle.entity.Raffle;
import com.be90z.domain.raffle.repository.RaffleRepository;
import com.be90z.domain.mission.entity.Mission;
import com.be90z.domain.mission.entity.MissionParticipation;
import com.be90z.domain.mission.entity.ParticipateStatus;
import com.be90z.domain.mission.repository.MissionRepository;
import com.be90z.domain.mission.repository.MissionParticipationRepository;
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
@DisplayName("RaffleController 테스트")
class RaffleControllerTest {


    @Autowired
    private RaffleRepository raffleRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionParticipationRepository participationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;
    private User testUser;
    private Raffle testRaffle;
    private Mission testMission;
    private MissionParticipation testParticipation;

    @BeforeEach
    void setUp() {
        // MockMvc는 @AutoConfigureMockMvc로 자동 설정됨

        // 테스트 사용자 생성
        testUser = User.builder()
                .provider("kakao")
                .nickname("테스트유저")
                .email("test@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .build();
        testUser = userRepository.save(testUser);

        // 테스트 미션 생성
        testMission = Mission.builder()
                .missionName("테스트 미션 제목")
                .missionContent("테스트 미션 내용")
                .missionGoalCount(1)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .build();
        testMission = missionRepository.save(testMission);

        // 테스트 미션 참여 생성
        testParticipation = MissionParticipation.builder()
                .participateStatus(ParticipateStatus.PART_COMPLETE)
                .participateCount(1)
                .user(testUser)
                .mission(testMission)
                .build();
        testParticipation = participationRepository.save(testParticipation);

        // 테스트 래플 생성
        testRaffle = Raffle.builder()
                .raffleCode(1L)
                .participation(testParticipation)
                .raffleName("1월 래플")
                .raffleDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        testRaffle = raffleRepository.save(testRaffle);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/v1/raffle - 래플 목록 조회 성공")
    void getRaffles_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/raffle")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].raffleName").value("1월 래플"))
            .andExpect(jsonPath("$[0].raffleCode").exists())
            .andExpect(jsonPath("$[0].participateCode").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/v1/raffle/winners - 래플 당첨자 목록 조회 성공")
    void getRaffleWinners_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/raffle/winners")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/v1/raffle/winners?raffleCode=123 - 특정 래플 당첨자 조회 성공")
    void getRaffleWinnersByCode_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/raffle/winners")
                .param("raffleCode", "123")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/v1/raffle/winners/user/{userId} - 사용자별 래플 당첨 내역 조회 성공")
    void getUserRaffleWinners_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/raffle/winners/user/{userId}", testUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/v1/raffle/winners/user/{userId} - 존재하지 않는 사용자로 조회 실패")
    void getUserRaffleWinners_UserNotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/raffle/winners/user/{userId}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/v1/raffle/draw/{raffleCode} - 래플 추첨 성공")
    void drawRaffle_Success() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/raffle/draw/{raffleCode}", testRaffle.getRaffleCode())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/v1/raffle/draw/{raffleCode} - 존재하지 않는 래플로 추첨 실패")
    void drawRaffle_RaffleNotFound() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/raffle/draw/{raffleCode}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}