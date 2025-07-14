package com.be90z.domain.mission.controller;

import com.be90z.domain.mission.dto.request.MissionJoinReqDTO;
import com.be90z.domain.mission.dto.response.MissionJoinResDTO;
import com.be90z.domain.mission.dto.response.MissionListResDTO;
import com.be90z.domain.mission.service.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mission")
@RequiredArgsConstructor
@Tag(name = "Mission", description = "미션 관련 API")
public class MissionController {

    private final MissionService missionService;

    @GetMapping("/list")
    @Operation(summary = "활성 미션 목록 조회", description = "현재 활성화된 미션 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<List<MissionListResDTO>> getActiveMissions() {
        List<MissionListResDTO> response = missionService.getActiveMissions();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/join")
    @Operation(summary = "미션 참여", description = "사용자가 미션에 참여합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "참여 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 미션을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<MissionJoinResDTO> joinMission(@RequestBody MissionJoinReqDTO request) {
        MissionJoinResDTO response = missionService.joinMission(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/status")
    @Operation(summary = "미션 상태 변경", description = "사용자의 미션 참여 상태를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 미션을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<Map<String, String>> updateMissionStatus(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @RequestParam Long userId,
            @Parameter(description = "미션 코드", required = true, example = "2")
            @RequestParam Long missionCode,
            @Parameter(description = "변경할 상태", required = true, example = "PART_COMPLETE")
            @RequestParam String status) {
        
        String updatedStatus = missionService.updateMissionStatus(userId, missionCode, status);
        return ResponseEntity.ok(Map.of("status", updatedStatus));
    }
}