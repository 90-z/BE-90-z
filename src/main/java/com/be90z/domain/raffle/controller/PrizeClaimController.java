package com.be90z.domain.raffle.controller;

import com.be90z.domain.raffle.dto.request.PrizeClaimReqDTO;
import com.be90z.domain.raffle.dto.response.PrizeClaimResDTO;
import com.be90z.domain.raffle.service.PrizeClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.be90z.domain.raffle.constants.PrizeClaimConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/raffle")
@RequiredArgsConstructor
@Tag(name = "상품 수령", description = "래플 상품 수령 및 다운로드 API")
public class PrizeClaimController {
    
    private final PrizeClaimService prizeClaimService;
    
    @PostMapping("/winners/{winnerCode}/claim")
    @Operation(summary = "상품 수령", description = "당첨자가 상품을 수령합니다.")
    public ResponseEntity<PrizeClaimResDTO> claimPrize(
            @Parameter(description = "당첨 코드", required = true)
            @PathVariable Long winnerCode,
            @Parameter(description = "상품 수령 요청 정보", required = true)
            @Valid @RequestBody PrizeClaimReqDTO request) {
        
        try {
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
    
    @GetMapping("/winners/{winnerCode}/download")
    @Operation(summary = "기프트카드 다운로드", description = "기프트카드 이미지를 다운로드합니다.")
    public ResponseEntity<byte[]> downloadGiftCard(
            @Parameter(description = "당첨 코드", required = true)
            @PathVariable Long winnerCode,
            @Parameter(description = "다운로드 토큰", required = true)
            @RequestParam String token) {
        
        try {
            byte[] giftCardData = prizeClaimService.downloadGiftCard(winnerCode, token);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(PrizeClaimConstants.CONTENT_TYPE_PNG));
            headers.setContentDispositionFormData("attachment", 
                PrizeClaimConstants.GIFT_CARD_FILE_PREFIX + winnerCode + PrizeClaimConstants.GIFT_CARD_FILE_EXTENSION);
            
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