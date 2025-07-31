package com.be90z.domain.raffle.scheduler;

import com.be90z.domain.raffle.entity.RaffleEntry;
import com.be90z.domain.raffle.entity.RaffleWinner;
import com.be90z.domain.raffle.repository.RaffleEntryRepository;
import com.be90z.domain.raffle.repository.RaffleWinnerRepository;
import com.be90z.domain.raffle.service.RaffleDrawService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonthlyRaffleScheduler {
    
    private static final int DEFAULT_WINNER_COUNT = 3;
    
    private final RaffleEntryRepository raffleEntryRepository;
    private final RaffleWinnerRepository raffleWinnerRepository;
    private final RaffleDrawService raffleDrawService;
    
    @Scheduled(cron = "0 59 23 L * ?") // 매월 마지막 날 23:59분
    public void executeMonthlyDraw() {
        log.info("월간 래플 추첨을 시작합니다.");
        
        try {
            // 활성 래플 참가자 조회
            List<RaffleEntry> activeEntries = raffleEntryRepository.findAllActiveEntries();
            
            if (activeEntries.isEmpty()) {
                log.info("이번 달 래플 참가자가 없습니다. 추첨을 건너뜁니다.");
                return;
            }
            
            log.info("총 {}명의 래플 참가자가 있습니다.", activeEntries.size());
            
            // 당첨자 추첨 (랜덤 3명)
            int winnerCount = DEFAULT_WINNER_COUNT;
            List<RaffleWinner> winners = raffleDrawService.drawWinners(activeEntries, winnerCount);
            
            // 당첨자 저장 및 기프티콘 데이터 업데이트
            if (!winners.isEmpty()) {
                raffleWinnerRepository.saveAll(winners);
                log.info("{}명의 당첨자가 선정되어 기프티콘 데이터가 업데이트되었습니다.", winners.size());
                
                // 당첨자 정보 로그
                winners.forEach(winner -> 
                    log.info("당첨자 ID: {}, 상품: {}", 
                        winner.getWinnerCode(), winner.getWinnerPrize())
                );
            }
            
            log.info("월간 래플 추첨이 완료되었습니다.");
            
        } catch (Exception e) {
            log.error("월간 래플 추첨 중 오류가 발생했습니다: {}", e.getMessage(), e);
        }
    }
}