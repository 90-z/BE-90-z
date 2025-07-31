package com.be90z.domain.raffle.service;

import com.be90z.domain.raffle.dto.request.PrizeClaimReqDTO;
import com.be90z.domain.raffle.dto.response.PrizeClaimResDTO;
import com.be90z.domain.raffle.entity.RaffleEntry;
import com.be90z.domain.raffle.entity.RaffleWinner;
import com.be90z.domain.raffle.repository.RaffleWinnerRepository;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.repository.UserRepository;
import com.be90z.domain.mission.entity.MissionParticipation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("상품 수령 서비스 테스트")
class PrizeClaimServiceTest {

    @Mock
    private RaffleWinnerRepository raffleWinnerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PrizeClaimService prizeClaimService;

    private User testUser;
    private RaffleWinner testWinner;
    private RaffleEntry testRaffleEntry;
    private MissionParticipation testParticipation;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .userId(1L)
            .nickname("TestUser")
            .email("test@example.com")
            .build();

        testParticipation = MissionParticipation.builder()
            .user(testUser)
            .build();

        testRaffleEntry = RaffleEntry.builder()
            .raffleCode(100L)
            .participation(testParticipation)
            .raffleName("Monthly Raffle 2024-01")
            .rafflePrizeCont("기프트카드 5000원")
            .raffleWinner(2)
            .raffleDate(LocalDateTime.now().plusDays(1))
            .createdAt(LocalDateTime.now())
            .build();

        testWinner = RaffleWinner.builder()
            .winnerCode(1L)
            .winnerPrize("기프트카드 5000원")
            .raffleEntry(testRaffleEntry)
            .build();
    }

    @Test
    @DisplayName("상품 수령이 성공적으로 처리되어야 한다")
    void shouldSuccessfullyClaimPrize() {
        // given
        PrizeClaimReqDTO request = PrizeClaimReqDTO.builder()
            .winnerCode(1L)
            .userId(1L)
            .claimMethod("GIFT_CARD")
            .recipientInfo("받는이 정보")
            .build();

        when(raffleWinnerRepository.findById(1L)).thenReturn(Optional.of(testWinner));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // when
        PrizeClaimResDTO result = prizeClaimService.claimPrize(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getWinnerCode()).isEqualTo(1L);
        assertThat(result.isClaimed()).isTrue();
        assertThat(result.getClaimDate()).isNotNull();
        assertThat(result.getPrizeName()).isEqualTo("기프트카드 5000원");
        assertThat(result.getGiftCardDownloadUrl()).isNotBlank();

        verify(raffleWinnerRepository).findById(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 당첨코드로 수령 시 예외가 발생해야 한다")
    void shouldThrowExceptionWhenWinnerNotFound() {
        // given
        PrizeClaimReqDTO request = PrizeClaimReqDTO.builder()
            .winnerCode(999L)
            .userId(1L)
            .claimMethod("GIFT_CARD")
            .recipientInfo("받는이 정보")
            .build();

        when(raffleWinnerRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> prizeClaimService.claimPrize(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("당첨 정보를 찾을 수 없습니다");

        verify(raffleWinnerRepository).findById(999L);
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 수령 시 예외가 발생해야 한다")
    void shouldThrowExceptionWhenUserNotFound() {
        // given
        PrizeClaimReqDTO request = PrizeClaimReqDTO.builder()
            .winnerCode(1L)
            .userId(999L)
            .claimMethod("GIFT_CARD")
            .recipientInfo("받는이 정보")
            .build();

        when(raffleWinnerRepository.findById(1L)).thenReturn(Optional.of(testWinner));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> prizeClaimService.claimPrize(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("사용자를 찾을 수 없습니다");

        verify(raffleWinnerRepository).findById(1L);
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("이미 수령한 상품을 다시 수령하려 할 때 예외가 발생해야 한다")
    void shouldThrowExceptionWhenPrizeAlreadyClaimed() {
        // given
        // 이미 수령된 상품을 시뮬레이션하기 위해 testWinner를 수정
        // 실제로는 RaffleWinner 엔티티에 claimed 필드가 있어야 함

        PrizeClaimReqDTO request = PrizeClaimReqDTO.builder()
            .winnerCode(1L)
            .userId(1L)
            .claimMethod("GIFT_CARD")
            .recipientInfo("받는이 정보")
            .build();

        when(raffleWinnerRepository.findById(1L)).thenReturn(Optional.of(testWinner));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // 이미 수령한 상태로 설정 (실제 구현에서는 엔티티에 상태 필드 추가 필요)
        when(prizeClaimService.isPrizeAlreadyClaimed(testWinner)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> prizeClaimService.claimPrize(request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("이미 수령한 상품입니다");
    }

    @Test
    @DisplayName("다른 사용자의 상품을 수령하려 할 때 예외가 발생해야 한다")
    void shouldThrowExceptionWhenUserMismatch() {
        // given
        User otherUser = User.builder()
            .userId(2L)
            .nickname("OtherUser")
            .email("other@example.com")
            .build();

        PrizeClaimReqDTO request = PrizeClaimReqDTO.builder()
            .winnerCode(1L)
            .userId(2L)  // 다른 사용자 ID
            .claimMethod("GIFT_CARD")
            .recipientInfo("받는이 정보")
            .build();

        when(raffleWinnerRepository.findById(1L)).thenReturn(Optional.of(testWinner));
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));

        // when & then
        assertThatThrownBy(() -> prizeClaimService.claimPrize(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("당첨자와 수령 요청자가 일치하지 않습니다");
    }

    @Test
    @DisplayName("잘못된 수령 방법으로 요청 시 예외가 발생해야 한다")
    void shouldThrowExceptionForInvalidClaimMethod() {
        // given
        PrizeClaimReqDTO request = PrizeClaimReqDTO.builder()
            .winnerCode(1L)
            .userId(1L)
            .claimMethod("INVALID_METHOD")  // 잘못된 수령 방법
            .recipientInfo("받는이 정보")
            .build();

        when(raffleWinnerRepository.findById(1L)).thenReturn(Optional.of(testWinner));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // when & then
        assertThatThrownBy(() -> prizeClaimService.claimPrize(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("지원하지 않는 수령 방법입니다");
    }

    @Test
    @DisplayName("기프트카드 다운로드 URL이 올바르게 생성되어야 한다")
    void shouldGenerateCorrectGiftCardDownloadUrl() {
        // given
        PrizeClaimReqDTO request = PrizeClaimReqDTO.builder()
            .winnerCode(1L)
            .userId(1L)
            .claimMethod("GIFT_CARD")
            .recipientInfo("받는이 정보")
            .build();

        when(raffleWinnerRepository.findById(1L)).thenReturn(Optional.of(testWinner));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // when
        PrizeClaimResDTO result = prizeClaimService.claimPrize(request);

        // then
        assertThat(result.getGiftCardDownloadUrl()).isNotNull();
        assertThat(result.getGiftCardDownloadUrl()).contains("/api/raffle/winners/1/download");
        assertThat(result.getGiftCardDownloadUrl()).contains("token=");
    }
}