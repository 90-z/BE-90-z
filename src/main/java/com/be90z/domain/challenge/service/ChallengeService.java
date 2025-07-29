//package com.be90z.domain.challenge.service;
//
//import com.be90z.domain.challenge.dto.response.ChallengeStatusResDTO;
//import com.be90z.domain.mission.entity.ParticipateStatus;
//import com.be90z.domain.mission.repository.MissionParticipationRepository;
//import com.be90z.domain.raffle.repository.RaffleEntryRepository;
//import com.be90z.domain.raffle.repository.RaffleWinnerRepository;
//import com.be90z.domain.user.entity.User;
//import com.be90z.domain.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.time.YearMonth;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class ChallengeService {
//
//    private final UserRepository userRepository;
//    private final MissionParticipationRepository missionParticipationRepository;
//    private final RaffleEntryRepository raffleEntryRepository;
//    private final RaffleWinnerRepository raffleWinnerRepository;
//
//    public ChallengeStatusResDTO getChallengeStatus(Long userId) {
//        // 사용자 존재 여부 확인
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
//
//        // 참여 중인 미션 수 조회 (PART_BEFORE 상태)
//        int participatingMissions = missionParticipationRepository
//                .countByUserIdAndParticipateStatus(userId, ParticipateStatus.PART_BEFORE);
//
//        // 래플 참여 횟수 (사용자별 전체 래플 엔트리 수)
//        int raffleParticipationCount = raffleEntryRepository
//                .countByParticipation_User_UserId(userId);
//
//        // 이번 달 전체 래플 참여자 수
//        YearMonth currentMonth = YearMonth.now();
//        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
//        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);
//
//        int totalRaffleParticipants = raffleEntryRepository
//                .countDistinctByCreatedAtBetween(startOfMonth, endOfMonth);
//
//        // 래플 당첨 횟수
//        int raffleWinCount = raffleWinnerRepository
//                .countByRaffleEntry_Participation_User_UserId(userId);
//
//        return ChallengeStatusResDTO.builder()
//                .participatingMissions(participatingMissions)
//                .raffleParticipationCount(raffleParticipationCount)
//                .totalRaffleParticipants(totalRaffleParticipants)
//                .raffleWinCount(raffleWinCount)
//                .build();
//    }
//}