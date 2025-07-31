package com.be90z.domain.raffle.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RaffleId implements Serializable {
    private Long raffleCode;
    private Long participateCode;
}