package com.be90z.domain.raffle.service;

import com.be90z.domain.raffle.dto.response.RaffleListResDTO;
import com.be90z.domain.raffle.dto.response.RaffleWinnerResDTO;
import com.be90z.domain.raffle.entity.Raffle;
import com.be90z.domain.raffle.repository.RaffleRepository;
import com.be90z.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RaffleService {

    private final RaffleRepository raffleRepository;
    private final UserRepository userRepository;

    public List<RaffleListResDTO> getAllRaffles() {
        List<Raffle> raffles = raffleRepository.findAll();
        return raffles.stream()
                .map(raffle -> RaffleListResDTO.builder()
                        .raffleCode(raffle.getRaffleCode())
                        .participateCode(raffle.getParticipateCode())
                        .raffleName(raffle.getRaffleName())
                        .raffleDate(raffle.getRaffleDate())
                        .createdAt(raffle.getCreatedAt())
                        .build())
                .toList();
    }

    public List<RaffleWinnerResDTO> getRaffleWinners(Long raffleCode) {
        // 실제 Raffle 엔티티를 사용하는 구현
        // 추후 실제 RaffleWinner 엔티티와 연결 시 수정 필요
        return new ArrayList<>();
    }

    public List<RaffleWinnerResDTO> getUserRaffleWinners(Long userId) {
        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        // 사용자별 당첨 내역 조회
        // 추후 실제 RaffleWinner 엔티티와 연결 시 수정 필요
        return new ArrayList<>();
    }

    @Transactional
    public List<RaffleWinnerResDTO> drawRaffle(Long raffleCode) {
        // 래플 존재 여부 확인
        // RaffleId를 사용하여 존재 여부 확인
        // 복합키이므로 실제로는 raffleCode와 participateCode 둘 다 필요
        if (raffleRepository.findAll().stream().noneMatch(r -> r.getRaffleCode().equals(raffleCode))) {
            throw new IllegalArgumentException("Raffle not found with id: " + raffleCode);
        }
        // 래플 추첨 로직
        // 추후 실제 비즈니스 로직 구현
        return new ArrayList<>();
    }
}