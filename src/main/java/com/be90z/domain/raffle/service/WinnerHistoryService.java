package com.be90z.domain.raffle.service;

import com.be90z.domain.raffle.dto.response.WinnerHistoryResDTO;
import com.be90z.domain.raffle.entity.RaffleWinner;
import com.be90z.domain.raffle.repository.RaffleWinnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WinnerHistoryService {
    
    private final RaffleWinnerRepository raffleWinnerRepository;
    
    /**
     * 전체 당첨내역을 페이징하여 조회합니다.
     */
    public Page<WinnerHistoryResDTO> getAllWinnerHistory(Pageable pageable) {
        log.debug("전체 당첨내역 조회 요청. page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<RaffleWinner> winnerPage = raffleWinnerRepository.findAllOrderByRaffleDateDesc(pageable);
        
        log.debug("전체 당첨내역 조회 완료. 총 {}건 중 {}건 반환", 
                 winnerPage.getTotalElements(), winnerPage.getContent().size());
        
        return winnerPage.map(this::convertToDTO);
    }
    
    /**
     * 특정 사용자의 당첨내역을 조회합니다.
     */
    public List<WinnerHistoryResDTO> getWinnerHistoryByUserId(Long userId) {
        log.debug("사용자별 당첨내역 조회 요청. userId: {}", userId);
        
        List<RaffleWinner> userWinners = raffleWinnerRepository
            .findByRaffleEntry_Participation_User_UserIdOrderByCreatedAtDesc(userId);
        
        log.debug("사용자별 당첨내역 조회 완료. userId: {}, 당첨내역 {}건", userId, userWinners.size());
        
        return userWinners.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * RaffleWinner 엔티티를 WinnerHistoryResDTO로 변환합니다.
     */
    private WinnerHistoryResDTO convertToDTO(RaffleWinner winner) {
        String userName = winner.getRaffleEntry().getParticipation().getUser().getNickname();
        LocalDateTime winDate = winner.getRaffleEntry().getCreatedAt();
        
        // 실제로는 상품 수령 상태를 추적하는 필드가 있어야 하지만
        // 현재는 기본값으로 처리
        boolean claimed = false;
        LocalDateTime claimDate = null;
        
        return WinnerHistoryResDTO.builder()
            .winnerCode(winner.getWinnerCode())
            .userName(userName)
            .prizeName(winner.getWinnerPrize())
            .winDate(winDate)
            .claimed(claimed)
            .claimDate(claimDate)
            .build();
    }
}