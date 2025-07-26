package com.be90z.domain.mission.service;

import com.be90z.domain.mission.dto.request.MissionJoinReqDTO;
import com.be90z.domain.mission.dto.response.MissionJoinResDTO;
import com.be90z.domain.mission.dto.response.MissionListResDTO;
import com.be90z.domain.mission.entity.Mission;
import com.be90z.domain.mission.entity.MissionParticipation;
import com.be90z.domain.mission.entity.ParticipateStatus;
import com.be90z.domain.mission.repository.MissionParticipationRepository;
import com.be90z.domain.mission.repository.MissionRepository;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {

    private final MissionRepository missionRepository;
    private final MissionParticipationRepository missionParticipationRepository;
    private final UserRepository userRepository;

    public List<MissionListResDTO> getActiveMissions() {
        List<Mission> missions = missionRepository.findAllByOrderByCreatedAtDesc();
        
        return missions.stream()
                .map(mission -> {
                    Long participantCount = missionParticipationRepository.countCompletedParticipationsByMission(mission);
                    return MissionListResDTO.builder()
                            .missionCode(mission.getMissionCode())
                            .missionContent(mission.getMissionContent())
                            .missionGoalCount(mission.getMissionGoalCount())
                            .startDate(mission.getStartDate())
                            .endDate(mission.getEndDate())
                            .currentParticipants(participantCount.intValue())
                            .build();
                })
                .toList();
    }

    @Transactional
    public MissionJoinResDTO joinMission(MissionJoinReqDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));
        
        Mission mission = missionRepository.findById(request.getMissionCode())
                .orElseThrow(() -> new IllegalArgumentException("Mission not found with code: " + request.getMissionCode()));
        
        Optional<MissionParticipation> existingParticipation = 
                missionParticipationRepository.findByUserAndMission(user, mission);
        
        if (existingParticipation.isPresent()) {
            throw new IllegalArgumentException("User already participated in this mission");
        }
        
        // Generate unique participate code based on current time
        Long participateCode = System.currentTimeMillis();
        
        MissionParticipation participation = MissionParticipation.builder()
                .participateCode(participateCode)
                .participateStatus(ParticipateStatus.PART_BEFORE)
                .participateCount(0)
                .user(user)
                .mission(mission)
                .build();
        
        MissionParticipation savedParticipation = missionParticipationRepository.save(participation);
        
        return MissionJoinResDTO.builder()
                .participateCode(savedParticipation.getParticipateCode())
                .message("미션 참여가 완료되었습니다.")
                .participateStatus(savedParticipation.getParticipateStatus().name())
                .build();
    }

    @Transactional
    public String updateMissionStatus(Long userId, Long missionCode, String newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        Mission mission = missionRepository.findById(missionCode)
                .orElseThrow(() -> new IllegalArgumentException("Mission not found with code: " + missionCode));
        
        MissionParticipation participation = missionParticipationRepository.findByUserAndMission(user, mission)
                .orElseThrow(() -> new IllegalArgumentException("User not participated in this mission"));
        
        ParticipateStatus status = ParticipateStatus.valueOf(newStatus);
        participation.updateStatus(status);
        
        return status.name();
    }
}