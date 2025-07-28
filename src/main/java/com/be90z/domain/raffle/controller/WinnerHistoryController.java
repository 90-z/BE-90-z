package com.be90z.domain.raffle.controller;

import com.be90z.domain.raffle.dto.response.WinnerHistoryResDTO;
import com.be90z.domain.raffle.service.WinnerHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/raffle")
@RequiredArgsConstructor
@Tag(name = "래플 당첨내역", description = "래플 당첨내역 조회 API")
public class WinnerHistoryController {
    
    private final WinnerHistoryService winnerHistoryService;
    
    @GetMapping("/winner-history")
    @Operation(summary = "래플 당첨내역 조회", description = "전체 당첨내역을 페이징하여 조회하거나 특정 사용자의 당첨내역을 조회합니다.")
    public ResponseEntity<?> getWinnerHistory(
            @Parameter(description = "사용자 ID (선택사항)")
            @RequestParam(required = false) Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기")
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            if (userId != null) {
                // 특정 사용자의 당첨내역 조회
                List<WinnerHistoryResDTO> userWinners = winnerHistoryService.getWinnerHistoryByUserId(userId);
                return ResponseEntity.ok(userWinners);
            } else {
                // 전체 당첨내역을 페이징하여 조회
                // 잘못된 페이지 파라미터 보정
                if (page < 0) page = 0;
                if (size <= 0) size = 20;
                
                Pageable pageable = PageRequest.of(page, size);
                Page<WinnerHistoryResDTO> winnerPage = winnerHistoryService.getAllWinnerHistory(pageable);
                return ResponseEntity.ok(winnerPage);
            }
        } catch (Exception e) {
            log.error("래플 당첨내역 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}