package com.be90z.domain.mission.controller;

import com.be90z.domain.mission.dto.request.MissionCreateReqDTO;
import com.be90z.domain.mission.dto.request.MissionJoinReqDTO;
import com.be90z.domain.mission.dto.request.MissionUpdateReqDTO;
import com.be90z.domain.mission.dto.response.MissionCreateResDTO;
import com.be90z.domain.mission.dto.response.MissionDetailResDTO;
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

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mission")
@RequiredArgsConstructor
@Tag(name = "Mission", description = "미션 관련 API")
public class MissionController {

    private final MissionService missionService;

    @GetMapping
    @Operation(summary = "활성 미션 목록 조회", description = "현재 활성화된 미션 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<List<MissionListResDTO>> getActiveMissions() {
        List<MissionListResDTO> response = missionService.getActiveMissions();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "미션 생성", description = "새로운 미션을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<MissionCreateResDTO> createMission(@Valid @RequestBody MissionCreateReqDTO request) {
        try {
            MissionCreateResDTO response = missionService.createMission(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{missionCode}")
    @Operation(summary = "미션 상세 조회", description = "특정 미션의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "미션을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<MissionDetailResDTO> getMissionDetail(
            @Parameter(description = "미션 코드", required = true, example = "1")
            @PathVariable Long missionCode,
            @Parameter(description = "사용자 ID (선택사항)", example = "1")
            @RequestParam(required = false) Long userId) {
        MissionDetailResDTO response = missionService.getMissionDetail(missionCode, userId);
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

    @PutMapping("/{missionCode}")
    @Operation(summary = "미션 수정", description = "기존 미션의 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "미션을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<MissionDetailResDTO> updateMission(
            @Parameter(description = "미션 코드", required = true, example = "1")
            @PathVariable Long missionCode,
            @Parameter(description = "미션 수정 정보", required = true)
            @Valid @RequestBody MissionUpdateReqDTO request) {
        
        try {
            MissionDetailResDTO response = missionService.updateMission(missionCode, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{missionCode}")
    @Operation(summary = "미션 삭제", description = "기존 미션을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "미션을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "참여자가 있어 삭제할 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<Void> deleteMission(
            @Parameter(description = "미션 코드", required = true, example = "1")
            @PathVariable Long missionCode) {
        
        try {
            missionService.deleteMission(missionCode);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).build(); // Conflict
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}