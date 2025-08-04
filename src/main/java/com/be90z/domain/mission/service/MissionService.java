package com.be90z.domain.mission.service;

import com.be90z.domain.mission.dto.request.MissionCreateReqDTO;
import com.be90z.domain.mission.dto.request.MissionJoinReqDTO;
import com.be90z.domain.mission.dto.request.MissionReplyReqDTO;
import com.be90z.domain.mission.dto.request.MissionRegistrationReqDTO;
import com.be90z.domain.mission.dto.request.MissionUpdateReqDTO;
import com.be90z.domain.mission.dto.response.MissionCreateResDTO;
import com.be90z.domain.mission.dto.response.MissionDetailResDTO;
import com.be90z.domain.mission.dto.response.MissionJoinResDTO;
import com.be90z.domain.mission.dto.response.MissionListResDTO;
import com.be90z.domain.mission.dto.response.MissionReplyResDTO;
import com.be90z.domain.mission.dto.response.MissionRegistrationResDTO;
import com.be90z.domain.mission.entity.Mission;
import com.be90z.domain.mission.entity.MissionParticipation;
import com.be90z.domain.mission.entity.MissionReply;
import com.be90z.domain.mission.entity.ParticipateStatus;
import com.be90z.domain.mission.repository.MissionParticipationRepository;
import com.be90z.domain.mission.repository.MissionReplyRepository;
import com.be90z.domain.mission.repository.MissionRepository;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.repository.UserRepository;
import com.be90z.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {

    private final MissionRepository missionRepository;
    private final MissionParticipationRepository missionParticipationRepository;
    private final MissionReplyRepository missionReplyRepository;
    private final UserRepository userRepository;

    @Transactional
    public MissionCreateResDTO createMission(MissionCreateReqDTO request) {
        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("종료일은 시작일보다 뒤여야 합니다");
        }
        
        Mission mission = Mission.builder()
                .missionName("기본 미션명") // missionName 필드 추가 (필수)
                .missionContent(request.getMissionContent())
                .missionGoalCount(request.getMissionGoalCount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        
        Mission savedMission = missionRepository.save(mission);
        
        return MissionCreateResDTO.builder()
                .missionCode(savedMission.getMissionCode())
                .missionContent(savedMission.getMissionContent())
                .missionGoalCount(savedMission.getMissionGoalCount())
                .startDate(savedMission.getStartDate())
                .endDate(savedMission.getEndDate())
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
                .orElseThrow(() -> new NotFoundException("User not found with id: " + request.getUserId()));
        
        Mission mission = missionRepository.findById(request.getMissionCode())
                .orElseThrow(() -> new NotFoundException("Mission not found with code: " + request.getMissionCode()));
        
        Optional<MissionParticipation> existingParticipation = 
                missionParticipationRepository.findByUserAndMission(user, mission);
        
        if (existingParticipation.isPresent()) {
            throw new IllegalArgumentException("User already participated in this mission");
        }
        
        MissionParticipation participation = MissionParticipation.builder()
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
                .orElseThrow(() -> new NotFoundException("Mission not found with code: " + missionCode));
        
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
                .missionName(mission.getMissionName())
                .missionContent(mission.getMissionContent())
                .missionGoalCount(mission.getMissionGoalCount())
                .startDate(mission.getStartDate())
                .endDate(mission.getEndDate())
                .currentParticipants(participantCount.intValue())
                .isParticipating(isParticipating)
                .participationStatus(participationStatus)
                .build();
    }

    @Transactional
    public String updateMissionStatus(Long userId, Long missionCode, String newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        
        Mission mission = missionRepository.findById(missionCode)
                .orElseThrow(() -> new NotFoundException("Mission not found with code: " + missionCode));
        
        MissionParticipation participation = missionParticipationRepository.findByUserAndMission(user, mission)
                .orElseThrow(() -> new NotFoundException("User not participated in this mission"));
        
        ParticipateStatus status = ParticipateStatus.valueOf(newStatus);
        participation.updateStatus(status);
        
        return status.name();
    }

    @Transactional
    public MissionDetailResDTO updateMission(Long missionCode, MissionUpdateReqDTO request) {
        Mission mission = missionRepository.findById(missionCode)
                .orElseThrow(() -> new NotFoundException("Mission not found with code: " + missionCode));
        
        // 명세서에 맞는 페이로드: {missionName, missionContent}
        mission.updateMission(request.getMissionName(), request.getMissionContent());
        
        Mission updatedMission = missionRepository.save(mission);
        
        // 업데이트된 미션 상세 정보 반환
        Long participantCount = missionParticipationRepository.countCompletedParticipationsByMission(updatedMission);
        
        return MissionDetailResDTO.builder()
                .missionCode(updatedMission.getMissionCode())
                .missionName(updatedMission.getMissionName())
                .missionContent(updatedMission.getMissionContent())
                .missionGoalCount(updatedMission.getMissionGoalCount())
                .startDate(updatedMission.getStartDate())
                .endDate(updatedMission.getEndDate())
                .currentParticipants(participantCount.intValue())
                .isParticipating(false)
                .participationStatus(null)
                .build();
    }

    @Transactional
    public void deleteMission(Long missionCode) {
        Mission mission = missionRepository.findById(missionCode)
                .orElseThrow(() -> new NotFoundException("Mission not found with code: " + missionCode));
        
        // 미션에 참여한 사용자가 있는지 확인
        Long participantCount = missionParticipationRepository.countCompletedParticipationsByMission(mission);
        if (participantCount > 0) {
            throw new IllegalStateException("참여자가 있는 미션은 삭제할 수 없습니다");
        }
        
        // 미션 삭제 (연관된 참여 데이터도 함께 삭제됨)
        missionParticipationRepository.deleteByMission(mission);
        missionRepository.delete(mission);
    }


    /**
     * 전체 활성 미션 조회 (명세: GET api/v1/mission)
     */
    public List<MissionListResDTO> getAllActiveMissions(int page, int count) {
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(page, count);
        Page<Mission> activeMissionsPage = missionRepository.findActiveMissions(now, pageable);
        
        return activeMissionsPage.getContent().stream()
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
    public MissionReplyResDTO createMissionReply(Long missionCode, MissionReplyReqDTO request) {
        // 미션 존재 확인
        Mission mission = missionRepository.findById(missionCode)
                .orElseThrow(() -> new NotFoundException("미션을 찾을 수 없습니다"));

        // 사용자 존재 확인
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다"));

        // 답글 내용 유효성 검사
        if (request.getReplyContent() == null || request.getReplyContent().trim().isEmpty()) {
            throw new IllegalArgumentException("답글 내용은 필수입니다");
        }

        // MissionReply 엔티티 생성 및 저장
        MissionReply missionReply = MissionReply.builder()
                .mission(mission)
                .user(user)
                .replyContent(request.getReplyContent())
                .createdAt(LocalDateTime.now())
                .build();

        MissionReply savedReply = missionReplyRepository.save(missionReply);

        // 응답 DTO 생성
        return MissionReplyResDTO.builder()
                .replyId(savedReply.getReplyId())
                .missionCode(mission.getMissionCode())
                .userId(user.getUserId())
                .replyContent(savedReply.getReplyContent())
                .createdAt(savedReply.getCreatedAt())
                .userNickname(user.getNickname())
                .build();
    }

    @Transactional
    public MissionRegistrationResDTO registerMissionChallenge(Long missionCode, MissionRegistrationReqDTO request) {
        // 미션 존재 확인
        Mission mission = missionRepository.findById(missionCode)
                .orElseThrow(() -> new NotFoundException("미션을 찾을 수 없습니다"));

        // 미션명 유효성 검사
        if (request.getMissionName() == null || request.getMissionName().trim().isEmpty()) {
            throw new IllegalArgumentException("미션명은 필수입니다");
        }

        // 미션 내용 유효성 검사
        if (request.getMissionContent() == null || request.getMissionContent().trim().isEmpty()) {
            throw new IllegalArgumentException("미션 내용은 필수입니다");
        }

        // 임시 ID 생성 (실제로는 새로운 엔티티를 저장해야 함)
        Long registrationId = System.currentTimeMillis() % 10000;

        // 응답 DTO 생성
        return MissionRegistrationResDTO.builder()
                .registrationId(registrationId)
                .missionCode(mission.getMissionCode())
                .missionName(request.getMissionName())
                .missionContent(request.getMissionContent())
                .createdAt(LocalDateTime.now())
                .build();
    }
}