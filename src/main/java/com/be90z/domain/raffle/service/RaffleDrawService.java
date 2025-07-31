package com.be90z.domain.raffle.service;

import com.be90z.domain.raffle.entity.RaffleEntry;
import com.be90z.domain.raffle.entity.RaffleWinner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class RaffleDrawService {
    
    private final Random random = new Random(System.currentTimeMillis());
    
    /**
     * 래플 참가자 목록에서 지정된 수만큼 당첨자를 추첨합니다.
     * 
     * @param entries 래플 참가자 목록
     * @param winnerCount 당첨자 수
     * @return 선정된 당첨자 목록
     */
    public List<RaffleWinner> drawWinners(List<RaffleEntry> entries, int winnerCount) {
        if (entries == null || entries.isEmpty()) {
            log.warn("래플 참가자가 없습니다.");
            return new ArrayList<>();
        }
        
        if (winnerCount <= 0) {
            log.warn("잘못된 당첨자 수입니다: {}", winnerCount);
            return new ArrayList<>();
        }
        
        // 참가자가 3명 미만일 경우 모든 참가자를 당첨자로 선정
        int actualWinnerCount = Math.min(entries.size(), winnerCount);
        
        if (entries.size() < winnerCount) {
            log.info("참가자가 {}명으로 {}명 미만이므로 모든 참가자가 당첨됩니다.", entries.size(), winnerCount);
        } else {
            log.info("{}명 중에서 랜덤으로 {}명의 당첨자를 추첨합니다.", entries.size(), actualWinnerCount);
        }
        
        // 참가자 목록을 복사하여 섞기
        List<RaffleEntry> shuffledEntries = new ArrayList<>(entries);
        Collections.shuffle(shuffledEntries, random);
        
        // 당첨자 생성
        List<RaffleWinner> winners = new ArrayList<>();
        LocalDateTime winDate = LocalDateTime.now();
        
        for (int i = 0; i < actualWinnerCount; i++) {
            RaffleEntry entry = shuffledEntries.get(i);
            
            RaffleWinner winner = RaffleWinner.builder()
                .winnerCode(System.currentTimeMillis() + i) // 임시 ID
                .winnerPrize("Monthly Raffle Gift Card") // 기프티콘 상품
                .raffleEntry(entry)
                .build();
                
            winners.add(winner);
        }
        
        log.info("당첨자 추첨이 완료되었습니다. 당첨자 수: {}", winners.size());
        
        return winners;
    }
}