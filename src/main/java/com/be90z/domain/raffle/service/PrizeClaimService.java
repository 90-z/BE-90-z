package com.be90z.domain.raffle.service;

import com.be90z.domain.raffle.dto.request.PrizeClaimReqDTO;
import com.be90z.domain.raffle.dto.response.PrizeClaimResDTO;
import com.be90z.domain.raffle.entity.RaffleWinner;
import com.be90z.domain.raffle.repository.RaffleWinnerRepository;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.be90z.domain.raffle.constants.PrizeClaimConstants;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PrizeClaimService {
    
    
    private final RaffleWinnerRepository raffleWinnerRepository;
    private final UserRepository userRepository;
    
    /**
     * 상품을 수령 처리합니다.
     */
    public PrizeClaimResDTO claimPrize(PrizeClaimReqDTO request) {
        // 1. 당첨 정보 조회
        RaffleWinner winner = raffleWinnerRepository.findById(request.getWinnerCode())
            .orElseThrow(() -> new IllegalArgumentException(
                PrizeClaimConstants.MSG_WINNER_NOT_FOUND + ": " + request.getWinnerCode()));
        
        // 2. 사용자 정보 조회
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException(
                PrizeClaimConstants.MSG_USER_NOT_FOUND + ": " + request.getUserId()));
        
        // 3. 당첨자와 요청자 일치 확인
        Long winnerUserId = winner.getRaffleEntry().getParticipation().getUser().getUserId();
        if (!winnerUserId.equals(request.getUserId())) {
            throw new IllegalArgumentException(PrizeClaimConstants.MSG_USER_MISMATCH);
        }
        
        // 4. 이미 수령한 상품인지 확인
        if (isPrizeAlreadyClaimed(winner)) {
            throw new IllegalStateException(PrizeClaimConstants.MSG_ALREADY_CLAIMED);
        }
        
        // 5. 수령 방법 검증
        if (!PrizeClaimConstants.VALID_CLAIM_METHODS.contains(request.getClaimMethod())) {
            throw new IllegalArgumentException(
                PrizeClaimConstants.MSG_INVALID_CLAIM_METHOD + ": " + request.getClaimMethod());
        }
        
        // 6. 상품 수령 처리 (실제로는 상품 수령 상태 업데이트)
        // 현재는 시뮬레이션으로 처리
        LocalDateTime claimDate = LocalDateTime.now();
        
        // 7. 기프트카드 다운로드 URL 생성
        String downloadUrl = generateGiftCardDownloadUrl(request.getWinnerCode());
        
        log.info("상품 수령 완료. 당첨코드: {}, 사용자: {}, 수령방법: {}", 
                request.getWinnerCode(), request.getUserId(), request.getClaimMethod());
        
        return PrizeClaimResDTO.builder()
            .winnerCode(request.getWinnerCode())
            .claimed(true)
            .claimDate(claimDate)
            .prizeName(winner.getWinnerPrize())
            .giftCardDownloadUrl(downloadUrl)
            .build();
    }
    
    /**
     * 기프트카드 이미지를 다운로드합니다.
     */
    @Transactional(readOnly = true)
    public byte[] downloadGiftCard(Long winnerCode, String token) {
        // 1. 토큰 검증
        if (!isValidDownloadToken(winnerCode, token)) {
            throw new IllegalArgumentException(PrizeClaimConstants.MSG_INVALID_TOKEN);
        }
        
        // 2. 토큰 만료 검증
        if (isDownloadTokenExpired(token)) {
            throw new IllegalStateException(PrizeClaimConstants.MSG_EXPIRED_TOKEN);
        }
        
        // 3. 당첨 정보 조회
        RaffleWinner winner = raffleWinnerRepository.findById(winnerCode)
            .orElseThrow(() -> new IllegalArgumentException(
                PrizeClaimConstants.MSG_WINNER_NOT_FOUND + ": " + winnerCode));
        
        // 4. 기프트카드 이미지 생성 (실제로는 외부 서비스 호출)
        return generateGiftCardImage(winner);
    }
    
    /**
     * 상품이 이미 수령되었는지 확인합니다.
     * 실제로는 RaffleWinner 엔티티에 claimed 필드가 있어야 합니다.
     */
    public boolean isPrizeAlreadyClaimed(RaffleWinner winner) {
        // 현재는 시뮬레이션으로 false 반환
        // 실제 구현에서는 winner.isClaimed() 같은 메서드 사용
        return false;
    }
    
    /**
     * 기프트카드 다운로드 URL을 생성합니다.
     */
    private String generateGiftCardDownloadUrl(Long winnerCode) {
        String token = generateDownloadToken(winnerCode);
        return PrizeClaimConstants.DOWNLOAD_URL_BASE + "/" + winnerCode + "/download?token=" + token;
    }
    
    /**
     * 다운로드 토큰을 생성합니다.
     */
    private String generateDownloadToken(Long winnerCode) {
        // 실제로는 JWT나 암호화된 토큰 생성
        return UUID.randomUUID().toString() + PrizeClaimConstants.TOKEN_DELIMITER + winnerCode;
    }
    
    /**
     * 다운로드 토큰이 유효한지 검증합니다.
     */
    private boolean isValidDownloadToken(Long winnerCode, String token) {
        // 실제로는 토큰 서명 검증 등
        return token != null && token.contains(winnerCode.toString());
    }
    
    /**
     * 다운로드 토큰이 만료되었는지 확인합니다.
     */
    private boolean isDownloadTokenExpired(String token) {
        // 실제로는 토큰의 만료시간 검증
        return false;
    }
    
    /**
     * 기프트카드 이미지를 생성합니다.
     */
    private byte[] generateGiftCardImage(RaffleWinner winner) {
        // 실제로는 이미지 생성 라이브러리 사용
        String content = "GIFT_CARD_IMAGE_DATA_FOR_" + winner.getWinnerCode();
        return content.getBytes();
    }
}