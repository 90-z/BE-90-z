package com.be90z.domain.mission.service;

import com.be90z.domain.mission.dto.response.ChallengeStatusResDTO;
import com.be90z.domain.mission.entity.MissionParticipation;
import com.be90z.domain.mission.repository.MissionParticipationRepository;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final UserRepository userRepository;
    private final MissionParticipationRepository participationRepository;

    public ChallengeStatusResDTO getChallengeStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        List<MissionParticipation> participations = participationRepository.findByUser(user);

        int participatingMissions = (int) participations.stream()
                .filter(p -> "PART_BEFORE".equals(p.getParticipateStatus().name()))
                .count();

        int completedMissions = (int) participations.stream()
                .filter(p -> "PART_COMPLETE".equals(p.getParticipateStatus().name()))
                .count();

        List<ChallengeStatusResDTO.MissionStatusDTO> missionList = participations.stream()
                .map(p -> ChallengeStatusResDTO.MissionStatusDTO.builder()
                        .missionCode(p.getMission().getMissionCode())
                        .missionName(p.getMission().getMissionName())
                        .participateStatus(p.getParticipateStatus().name())
                        .progress(getProgressString(p))
                        .build())
                .toList();

        // 래플 당첨 내역은 추후 구현
        List<ChallengeStatusResDTO.RaffleWinnerStatusDTO> winnerHistory = new ArrayList<>();

        return ChallengeStatusResDTO.builder()
                .userId(userId)
                .nickname(user.getNickname())
                .participatingMissions(participatingMissions)
                .completedMissions(completedMissions)
                .raffleEntries(completedMissions) // 완료된 미션 수만큼 래플 참여
                .missionList(missionList)
                .winnerHistory(winnerHistory)
                .build();
    }

    private String getProgressString(MissionParticipation participation) {
        // 추후 횟수 미션에 대한 진행률 계산 로직 구현
        return participation.getParticipateStatus().name().equals("PART_COMPLETE") ? "완료" : "진행 중";
    }
}