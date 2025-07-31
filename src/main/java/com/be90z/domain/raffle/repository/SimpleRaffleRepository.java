package com.be90z.domain.raffle.repository;

import com.be90z.domain.raffle.entity.SimpleRaffle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimpleRaffleRepository extends JpaRepository<SimpleRaffle, Long> {
}