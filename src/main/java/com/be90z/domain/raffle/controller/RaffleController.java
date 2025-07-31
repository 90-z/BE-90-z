package com.be90z.domain.raffle.controller;

import com.be90z.domain.raffle.dto.response.RaffleListResDTO;
import com.be90z.domain.raffle.dto.response.RaffleWinnerResDTO;
import com.be90z.domain.raffle.service.RaffleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/raffle")
@RequiredArgsConstructor
@Tag(name = "Raffle", description = "래플 관련 API")
public class RaffleController {

    private final RaffleService raffleService;

    @GetMapping
    @Operation(summary = "래플 목록 조회", description = "현재 진행 중인 래플 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<List<RaffleListResDTO>> getRaffles() {
        List<RaffleListResDTO> response = raffleService.getAllRaffles();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/winners")
    @Operation(summary = "래플 당첨자 조회", description = "래플 당첨자 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<List<RaffleWinnerResDTO>> getRaffleWinners(
            @Parameter(description = "래플 코드 (선택사항)", example = "202501")
            @RequestParam(required = false) Long raffleCode) {
        List<RaffleWinnerResDTO> response = raffleService.getRaffleWinners(raffleCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/winners/user/{userId}")
    @Operation(summary = "사용자별 래플 당첨 내역 조회", description = "특정 사용자의 래플 당첨 내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<List<RaffleWinnerResDTO>> getUserRaffleWinners(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @PathVariable Long userId) {
        try {
            List<RaffleWinnerResDTO> response = raffleService.getUserRaffleWinners(userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/draw/{raffleCode}")
    @Operation(summary = "래플 추첨", description = "래플 추첨을 실행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추첨 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "래플을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<List<RaffleWinnerResDTO>> drawRaffle(
            @Parameter(description = "래플 코드", required = true, example = "202501")
            @PathVariable Long raffleCode) {
        try {
            List<RaffleWinnerResDTO> winners = raffleService.drawRaffle(raffleCode);
            return ResponseEntity.ok(winners);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}