package com.be90z.domain.raffle.controller;

import com.be90z.domain.raffle.dto.request.PrizeClaimReqDTO;
import com.be90z.domain.raffle.dto.response.PrizeClaimResDTO;
import com.be90z.domain.raffle.dto.response.RaffleStatusResDTO;
import com.be90z.domain.raffle.dto.response.WinnerHistoryResDTO;
import com.be90z.domain.raffle.service.PrizeClaimService;
import com.be90z.domain.raffle.service.RaffleService;
import com.be90z.domain.raffle.service.WinnerHistoryService;
import com.be90z.domain.raffle.constants.PrizeClaimConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/raffle")
@RequiredArgsConstructor
@Tag(name = "Raffle", description = "래플 통합 관리 API")
public class RaffleController {

    private final RaffleService raffleService;
    private final PrizeClaimService prizeClaimService;
    private final WinnerHistoryService winnerHistoryService;

    // ==================== 기본 래플 기능 ====================
    
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

    @PostMapping("/entries")
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

    // ==================== 당첨자 관리 (통합) ====================
    
    @GetMapping("/winners")
    @Operation(summary = "래플 당첨자 조회", description = "래플 당첨자 목록을 조회합니다. 기간별, 사용자별 조회 가능.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<?> getWinners(
            @Parameter(description = "조회 기간 (current: 이번 달, history: 전체 내역)")
            @RequestParam(defaultValue = "current") String period,
            @Parameter(description = "사용자 ID (선택사항 - 특정 사용자 당첨내역 조회)")
            @RequestParam(required = false) Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기")
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            if ("current".equals(period)) {
                // 이번 달 당첨자 조회
                List<String> winners = raffleService.getMonthlyWinners();
                return ResponseEntity.ok(Map.of(
                    "period", "current",
                    "winners", winners,
                    "count", winners.size()
                ));
            } else if ("history".equals(period)) {
                // 당첨 내역 조회
                if (userId != null) {
                    // 특정 사용자의 당첨내역 조회
                    List<WinnerHistoryResDTO> userWinners = winnerHistoryService.getWinnerHistoryByUserId(userId);
                    return ResponseEntity.ok(Map.of(
                        "period", "history",
                        "userId", userId,
                        "winners", userWinners,
                        "count", userWinners.size()
                    ));
                } else {
                    // 전체 당첨내역을 페이징하여 조회
                    if (page < 0) page = 0;
                    if (size <= 0) size = 20;
                    
                    Pageable pageable = PageRequest.of(page, size);
                    Page<WinnerHistoryResDTO> winnerPage = winnerHistoryService.getAllWinnerHistory(pageable);
                    return ResponseEntity.ok(winnerPage);
                }
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid period parameter",
                    "validValues", List.of("current", "history")
                ));
            }
        } catch (Exception e) {
            log.error("래플 당첨자 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==================== 상품 수령 ====================
    
    @PostMapping("/winners/{winnerId}/claims")
    @Operation(summary = "상품 수령", description = "당첨자가 상품을 수령합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수령 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "당첨 정보를 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "이미 수령한 상품"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<PrizeClaimResDTO> claimPrize(
            @Parameter(description = "당첨자 ID", required = true)
            @PathVariable Long winnerId,
            @Parameter(description = "상품 수령 요청 정보", required = true)
            @Valid @RequestBody PrizeClaimReqDTO request) {
        
        try {
            // 요청 DTO에 winnerId 설정 (경로 파라미터와 일치시키기 위해)
            request.setWinnerCode(winnerId);
            
            PrizeClaimResDTO response = prizeClaimService.claimPrize(request);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("상품 수령 요청 오류: {}", e.getMessage());
            return ResponseEntity.notFound().build();
            
        } catch (IllegalStateException e) {
            log.warn("상품 수령 상태 오류: {}", e.getMessage());
            return ResponseEntity.status(409).build(); // Conflict
            
        } catch (Exception e) {
            log.error("상품 수령 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/winners/{winnerId}/downloads")
    @Operation(summary = "기프트카드 다운로드", description = "기프트카드 이미지를 다운로드합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "다운로드 성공"),
        @ApiResponse(responseCode = "403", description = "잘못된 토큰"),
        @ApiResponse(responseCode = "404", description = "당첨 정보를 찾을 수 없음"),
        @ApiResponse(responseCode = "410", description = "다운로드 만료"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ResponseEntity<byte[]> downloadGiftCard(
            @Parameter(description = "당첨자 ID", required = true)
            @PathVariable Long winnerId,
            @Parameter(description = "다운로드 토큰", required = true)
            @RequestParam String token) {
        
        try {
            byte[] giftCardData = prizeClaimService.downloadGiftCard(winnerId, token);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(PrizeClaimConstants.CONTENT_TYPE_PNG));
            headers.setContentDispositionFormData("attachment", 
                PrizeClaimConstants.GIFT_CARD_FILE_PREFIX + winnerId + PrizeClaimConstants.GIFT_CARD_FILE_EXTENSION);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(giftCardData);
                
        } catch (IllegalArgumentException e) {
            log.warn("기프트카드 다운로드 토큰 오류: {}", e.getMessage());
            return ResponseEntity.status(403).build(); // Forbidden
            
        } catch (IllegalStateException e) {
            log.warn("기프트카드 다운로드 만료: {}", e.getMessage());
            return ResponseEntity.status(410).build(); // Gone
            
        } catch (Exception e) {
            log.error("기프트카드 다운로드 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}