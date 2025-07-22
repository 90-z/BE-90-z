package com.be90z.domain.challenge.service;

import com.be90z.domain.challenge.dto.response.ChallengeStatusResDTO;
import com.be90z.domain.mission.entity.ParticipateStatus;
import com.be90z.domain.mission.repository.MissionParticipationRepository;
import com.be90z.domain.raffle.repository.RaffleEntryRepository;
import com.be90z.domain.raffle.repository.RaffleWinnerRepository;
import com.be90z.domain.user.entity.Gender;
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
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private MissionParticipationRepository missionParticipationRepository;
    
    @Mock
    private RaffleEntryRepository raffleEntryRepository;
    
    @Mock
    private RaffleWinnerRepository raffleWinnerRepository;

    @InjectMocks
    private ChallengeService challengeService;

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 챌린지 현황 조회시 예외 발생")
    void getChallengeStatus_UserNotFound_ThrowsException() {
        // given
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> challengeService.getChallengeStatus(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found with id: " + userId);
    }

    @Test
    @DisplayName("유효한 사용자 ID로 챌린지 현황 조회 성공")
    void getChallengeStatus_ValidUserId_ReturnsStatus() {
        // given
        Long userId = 1L;
        User mockUser = User.builder()
                .userId(userId)
                .provider("test_provider")
                .nickname("test_user")
                .email("test@example.com")
                .auth(UserAuthority.USER)
                .gender(Gender.MAN)
                .birth(1990)
                .createdAt(LocalDateTime.now())
                .build();
        
        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
        given(missionParticipationRepository.countByUserIdAndParticipateStatus(userId, ParticipateStatus.PART_BEFORE))
                .willReturn(3);

        // when
        ChallengeStatusResDTO result = challengeService.getChallengeStatus(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getParticipatingMissions()).isEqualTo(3);
        assertThat(result.getRaffleParticipationCount()).isEqualTo(0); // 현재 구현에서는 0
        assertThat(result.getTotalRaffleParticipants()).isEqualTo(0); // 현재 구현에서는 0
        assertThat(result.getRaffleWinCount()).isEqualTo(0); // 현재 구현에서는 0
    }
}