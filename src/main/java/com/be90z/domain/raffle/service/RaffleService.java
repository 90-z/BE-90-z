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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RaffleService {
    
    private static final int DEFAULT_WINNER_COUNT = 2;
    private static final String MONTHLY_RAFFLE_PREFIX = "Monthly Raffle ";
    private static final String DEFAULT_PRIZE_CONTENT = "Monthly Prize Package";
    
    private final RaffleEntryRepository raffleEntryRepository;
    private final RaffleWinnerRepository raffleWinnerRepository;
    private final MissionRepository missionRepository;
    private final MissionParticipationRepository participationRepository;
    private final UserRepository userRepository;

    /**
     * 사용자의 래플 참가 상태를 조회합니다.
     */
    public RaffleStatusResDTO getRaffleStatus(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
            
        // 사용자의 래플 참가 횟수 조회
        int entryCount = raffleEntryRepository.countByParticipation_User_UserId(userId);
        
        // 마지막 참가 날짜 조회
        Optional<RaffleEntry> lastEntry = raffleEntryRepository
            .findTopByParticipation_User_UserIdOrderByCreatedAtDesc(userId);
        LocalDateTime lastEntryDate = lastEntry.map(RaffleEntry::getCreatedAt).orElse(null);
        
        // 다음 추첨일 계산 (매월 마지막 날)
        LocalDateTime nextDrawDate = calculateNextDrawDate();
        
        boolean isParticipating = entryCount > 0;
        
        return RaffleStatusResDTO.of(isParticipating, entryCount, lastEntryDate, nextDrawDate);
    }

    /**
     * 미션 완료 시 래플에 참가시킵니다.
     */
    @Transactional
    public boolean joinRaffle(Long userId, Long missionId) {
        try {
            User user = userRepository.findById(userId)
                .orElse(null);
            if (user == null) {
                log.warn("사용자를 찾을 수 없습니다: {}", userId);
                return false;
            }
            
            Mission mission = missionRepository.findById(missionId)
                .orElse(null);
            if (mission == null) {
                log.warn("미션을 찾을 수 없습니다: {}", missionId);
                return false;
            }
            
            // 미션 참가 정보 조회
            Optional<MissionParticipation> participationOpt = 
                participationRepository.findByUserAndMission(user, mission);
            if (participationOpt.isEmpty()) {
                log.warn("미션 참가 정보를 찾을 수 없습니다. userId: {}, missionId: {}", userId, missionId);
                return false;
            }
            
            MissionParticipation participation = participationOpt.get();
            
            // 래플 엔트리 생성
            RaffleEntry raffleEntry = RaffleEntry.builder()
                .raffleCode(generateRaffleCode())
                .participation(participation)
                .raffleName(MONTHLY_RAFFLE_PREFIX + YearMonth.now())
                .rafflePrizeCont(DEFAULT_PRIZE_CONTENT)
                .raffleWinner(DEFAULT_WINNER_COUNT)
                .raffleDate(calculateNextDrawDate())
                .createdAt(LocalDateTime.now())
                .build();
                
            raffleEntryRepository.save(raffleEntry);
            
            log.info("래플 참가 완료. userId: {}, missionId: {}", userId, missionId);
            return true;
            
        } catch (Exception e) {
            log.error("래플 참가 중 오류 발생. userId: {}, missionId: {}, error: {}", 
                     userId, missionId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 이번 달 당첨자 목록을 조회합니다.
     */
    public List<String> getMonthlyWinners() {
        List<RaffleWinner> winners = raffleWinnerRepository.findThisMonthWinners();
        
        return winners.stream()
            .map(winner -> {
                // RaffleWinner -> 사용자 닉네임 추출 (실제로는 연관관계를 통해 조회)
                // 임시로 "User" + winnerCode 반환
                return "User" + (winner.getWinnerCode() % 1000);
            })
            .toList();
    }

    /**
     * 다음 래플 추첨일을 계산합니다. (매월 마지막 날 자정)
     */
    private LocalDateTime calculateNextDrawDate() {
        YearMonth currentMonth = YearMonth.now();
        return currentMonth.atEndOfMonth().atStartOfDay();
    }
    
    /**
     * 고유한 래플 코드를 생성합니다.
     */
    private Long generateRaffleCode() {
        return System.currentTimeMillis() + (long)(Math.random() * 1000);
    }
}