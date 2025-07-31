package com.be90z.domain.raffle.service;

import com.be90z.domain.mission.entity.Mission;
import com.be90z.domain.mission.entity.MissionParticipation;
import com.be90z.domain.mission.repository.MissionParticipationRepository;
import com.be90z.domain.mission.repository.MissionRepository;
import com.be90z.domain.raffle.dto.response.RaffleStatusResDTO;
import com.be90z.domain.raffle.entity.RaffleEntry;
import com.be90z.domain.raffle.entity.RaffleWinner;
import com.be90z.domain.raffle.repository.RaffleEntryRepository;
import com.be90z.domain.raffle.repository.RaffleWinnerRepository;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("래플 서비스 테스트")
class RaffleServiceTest {

    @Mock
    private RaffleEntryRepository raffleEntryRepository;
    
    @Mock
    private RaffleWinnerRepository raffleWinnerRepository;
    
    @Mock
    private MissionRepository missionRepository;
    
    @Mock
    private MissionParticipationRepository participationRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private RaffleService raffleService;

    @Test
    @DisplayName("사용자의 래플 상태를 정확히 조회해야 한다")
    void shouldGetRaffleStatusCorrectly() {
        // given
        Long userId = 1L;
        User user = createUser(userId);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(raffleEntryRepository.countByParticipation_User_UserId(userId)).thenReturn(3);
        when(raffleEntryRepository.findTopByParticipation_User_UserIdOrderByCreatedAtDesc(userId))
            .thenReturn(Optional.of(createRaffleEntry()));

        // when
        RaffleStatusResDTO result = raffleService.getRaffleStatus(userId);

        // then
        assertThat(result.isParticipating()).isTrue();
        assertThat(result.getEntryCount()).isEqualTo(3);
        assertThat(result.getLastEntryDate()).isNotNull();
        assertThat(result.getNextDrawDate()).isNotNull();
    }

    @Test
    @DisplayName("미션 완료 시 래플에 자동 참가해야 한다")
    void shouldJoinRaffleWhenMissionCompleted() {
        // given
        Long userId = 1L;
        Long missionId = 1L;
        
        User user = createUser(userId);
        Mission mission = createMission(missionId);
        MissionParticipation participation = createParticipation(user, mission);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(missionRepository.findById(missionId)).thenReturn(Optional.of(mission));
        when(participationRepository.findByUserAndMission(user, mission))
            .thenReturn(Optional.of(participation));
        when(raffleEntryRepository.save(any(RaffleEntry.class)))
            .thenReturn(createRaffleEntry());

        // when
        boolean result = raffleService.joinRaffle(userId, missionId);

        // then
        assertThat(result).isTrue();
        verify(raffleEntryRepository).save(any(RaffleEntry.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자는 래플에 참가할 수 없다")
    void shouldNotJoinRaffleForNonExistentUser() {
        // given
        Long userId = 999L;
        Long missionId = 1L;
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        boolean result = raffleService.joinRaffle(userId, missionId);

        // then
        assertThat(result).isFalse();
        verify(raffleEntryRepository, never()).save(any());
    }

    @Test
    @DisplayName("이번 달 당첨자 목록을 조회할 수 있어야 한다")
    void shouldGetMonthlyWinners() {
        // given
        List<RaffleWinner> winners = Arrays.asList(
            createRaffleWinner("User1"),
            createRaffleWinner("User2")
        );
        
        when(raffleWinnerRepository.findThisMonthWinners()).thenReturn(winners);

        // when
        List<String> result = raffleService.getMonthlyWinners();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).contains("User1", "User2");
    }

    @Test
    @DisplayName("래플 참가자가 없으면 빈 상태를 반환해야 한다")
    void shouldReturnEmptyStatusForNoParticipant() {
        // given
        Long userId = 1L;
        User user = createUser(userId);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(raffleEntryRepository.countByParticipation_User_UserId(userId)).thenReturn(0);
        when(raffleEntryRepository.findTopByParticipation_User_UserIdOrderByCreatedAtDesc(userId))
            .thenReturn(Optional.empty());

        // when
        RaffleStatusResDTO result = raffleService.getRaffleStatus(userId);

        // then
        assertThat(result.isParticipating()).isFalse();
        assertThat(result.getEntryCount()).isEqualTo(0);
        assertThat(result.getLastEntryDate()).isNull();
        assertThat(result.getNextDrawDate()).isNotNull(); // 다음 추첨일은 항상 있음
    }

    // Helper methods
    private User createUser(Long userId) {
        return User.builder()
            .userId(userId)
            .provider("test")
            .nickname("testUser")
            .email("test@test.com")
            .build();
    }

    private Mission createMission(Long missionId) {
        return Mission.builder()
            .missionCode(missionId)
            .build();
    }

    private MissionParticipation createParticipation(User user, Mission mission) {
        return MissionParticipation.builder()
            .participateCode(1L)
            .user(user)
            .mission(mission)
            .participatedAt(LocalDateTime.now())
            .build();
    }

    private RaffleEntry createRaffleEntry() {
        return RaffleEntry.builder()
            .raffleCode(1L)
            .raffleName("Test Raffle")
            .raffleDate(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();
    }

    private RaffleWinner createRaffleWinner(String nickname) {
        return RaffleWinner.builder()
            .winnerCode(System.currentTimeMillis())
            .winnerPrize("Monthly Prize")
            .raffleEntry(createRaffleEntry())
            .build();
    }
}