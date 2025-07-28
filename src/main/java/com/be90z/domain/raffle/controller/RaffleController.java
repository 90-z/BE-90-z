package com.be90z.domain.raffle.controller;

import com.be90z.domain.raffle.dto.response.RaffleStatusResDTO;
import com.be90z.domain.raffle.service.RaffleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/raffle")
@RequiredArgsConstructor
@Tag(name = "Raffle", description = "래플 관련 API")
public class RaffleController {

    private final RaffleService raffleService;

    @GetMapping("/status")
    @Operation(summary = "래플 상태 조회", description = "사용자의 래플 참가 상태를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<RaffleStatusResDTO> getRaffleStatus(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId) {
        
        try {
            RaffleStatusResDTO status = raffleService.getRaffleStatus(userId);
            return ResponseEntity.ok(status);
        } catch (IllegalArgumentException e) {
            log.warn("사용자를 찾을 수 없습니다: {}", userId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("래플 상태 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/join")
    @Operation(summary = "래플 참가", description = "미션 완료 시 래플에 참가합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "참가 성공"),
        @ApiResponse(responseCode = "400", description = "참가 실패"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<Map<String, Object>> joinRaffle(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "미션 ID", required = true)
            @RequestParam Long missionId) {
        
        try {
            boolean success = raffleService.joinRaffle(userId, missionId);
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "래플 참가가 완료되었습니다."
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "래플 참가에 실패했습니다."
                ));
            }
        } catch (Exception e) {
            log.error("래플 참가 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "서버 오류가 발생했습니다."
            ));
        }
    }

    @GetMapping("/current-winners")
    @Operation(summary = "이번 달 래플 당첨자 조회", description = "이번 달 래플 당첨자 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<List<String>> getRaffleWinners() {
        try {
            List<String> winners = raffleService.getMonthlyWinners();
            return ResponseEntity.ok(winners);
        } catch (Exception e) {
            log.error("래플 당첨자 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}