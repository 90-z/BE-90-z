package com.be90z.domain.mission.service;

import com.be90z.domain.mission.dto.response.MissionListResDTO;
import com.be90z.domain.mission.entity.Mission;
import com.be90z.domain.mission.repository.MissionRepository;
import com.be90z.domain.mission.repository.MissionParticipationRepository;
import com.be90z.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("MissionService 테스트")
class MissionServiceTest {

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private MissionParticipationRepository missionParticipationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MissionService missionService;

    @Test
    @DisplayName("전체 미션 조회 - 활성 상태 미션들을 최신순으로 반환")
    void getAllActiveMissions() {
        // given
        Mission mission1 = Mission.builder()
                .missionCode(1L)
                .missionName("물 마시기 미션")
                .missionContent("하루에 물을 8잔 이상 마시기")
                .missionGoalCount(100)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

        Mission mission2 = Mission.builder()
                .missionCode(2L)
                .missionName("운동 미션")
                .missionContent("하루 30분 이상 운동하기")
                .missionGoalCount(50)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        given(missionRepository.findActiveMissions(any(LocalDateTime.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(Arrays.asList(mission2, mission1))); // 최신순

        // when
        List<MissionListResDTO> result = missionService.getAllActiveMissions(0, 10);

        // then
        assertThat(result).hasSize(2);
        
        MissionListResDTO dto1 = result.get(0);
        assertThat(dto1.getMissionContent()).isEqualTo("하루 30분 이상 운동하기");
        assertThat(dto1.getMissionGoalCount()).isEqualTo(50);
        
        MissionListResDTO dto2 = result.get(1);
        assertThat(dto2.getMissionContent()).isEqualTo("하루에 물을 8잔 이상 마시기");
        assertThat(dto2.getMissionGoalCount()).isEqualTo(100);
    }
}