package com.be90z.domain.mission.service;

import com.be90z.domain.mission.dto.request.MissionCreateReqDTO;
import com.be90z.domain.mission.dto.request.MissionJoinReqDTO;
import com.be90z.domain.mission.dto.request.MissionUpdateReqDTO;
import com.be90z.domain.mission.dto.response.MissionCreateResDTO;
import com.be90z.domain.mission.dto.response.MissionDetailResDTO;
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

    @Transactional
    public MissionCreateResDTO createMission(MissionCreateReqDTO request) {
        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("종료일은 시작일보다 뒤여야 합니다");
        }
        
        Mission mission = Mission.builder()
                .missionContent(request.getMissionContent())
                .missionGoalCount(request.getMissionGoalCount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .maxParticipants(request.getMaxParticipants())
                .build();
        
        Mission savedMission = missionRepository.save(mission);
        
        return MissionCreateResDTO.builder()
                .missionCode(savedMission.getMissionCode())
                .missionContent(savedMission.getMissionContent())
                .missionGoalCount(savedMission.getMissionGoalCount())
                .startDate(savedMission.getStartDate())
                .endDate(savedMission.getEndDate())
                .maxParticipants(savedMission.getMaxParticipants())
                .createdAt(savedMission.getCreatedAt())
                .build();
    }

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

    public MissionDetailResDTO getMissionDetail(Long missionCode, Long userId) {
        Mission mission = missionRepository.findById(missionCode)
                .orElseThrow(() -> new IllegalArgumentException("Mission not found with code: " + missionCode));
        
        Long participantCount = missionParticipationRepository.countCompletedParticipationsByMission(mission);
        
        Boolean isParticipating = false;
        String participationStatus = null;
        
        if (userId != null) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                Optional<MissionParticipation> participation = 
                        missionParticipationRepository.findByUserAndMission(userOpt.get(), mission);
                
                if (participation.isPresent()) {
                    isParticipating = true;
                    participationStatus = participation.get().getParticipateStatus().name();
                }
            }
        }
        
        return MissionDetailResDTO.builder()
                .missionCode(mission.getMissionCode())
                .missionContent(mission.getMissionContent())
                .missionGoalCount(mission.getMissionGoalCount())
                .startDate(mission.getStartDate())
                .endDate(mission.getEndDate())
                .currentParticipants(participantCount.intValue())
                .maxParticipants(mission.getMaxParticipants())
                .status("ACTIVE")
                .isParticipating(isParticipating)
                .participationStatus(participationStatus)
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

    @Transactional
    public MissionDetailResDTO updateMission(Long missionCode, MissionUpdateReqDTO request) {
        Mission mission = missionRepository.findById(missionCode)
                .orElseThrow(() -> new IllegalArgumentException("Mission not found with code: " + missionCode));
        
        // 날짜 유효성 검증
        if (request.getEndDate() != null && request.getStartDate() != null && 
            request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("종료일은 시작일보다 뒤여야 합니다");
        }
        
        mission.updateMission(
            request.getMissionContent(),
            request.getMissionGoalCount(),
            request.getStartDate(),
            request.getEndDate(),
            request.getMaxParticipants()
        );
        
        Mission updatedMission = missionRepository.save(mission);
        
        // 업데이트된 미션 상세 정보 반환
        Long participantCount = missionParticipationRepository.countCompletedParticipationsByMission(updatedMission);
        
        return MissionDetailResDTO.builder()
                .missionCode(updatedMission.getMissionCode())
                .missionContent(updatedMission.getMissionContent())
                .missionGoalCount(updatedMission.getMissionGoalCount())
                .startDate(updatedMission.getStartDate())
                .endDate(updatedMission.getEndDate())
                .currentParticipants(participantCount.intValue())
                .maxParticipants(updatedMission.getMaxParticipants())
                .status("ACTIVE")
                .isParticipating(false)
                .participationStatus(null)
                .build();
    }

    @Transactional
    public void deleteMission(Long missionCode) {
        Mission mission = missionRepository.findById(missionCode)
                .orElseThrow(() -> new IllegalArgumentException("Mission not found with code: " + missionCode));
        
        // 미션에 참여한 사용자가 있는지 확인
        Long participantCount = missionParticipationRepository.countCompletedParticipationsByMission(mission);
        if (participantCount > 0) {
            throw new IllegalStateException("참여자가 있는 미션은 삭제할 수 없습니다");
        }
        
        // 미션 삭제 (연관된 참여 데이터도 함께 삭제됨)
        missionParticipationRepository.deleteByMission(mission);
        missionRepository.delete(mission);
    }
}