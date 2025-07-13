package com.be90z.domain.raffle.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RaffleEntryId implements Serializable {
    private Long raffleCode;
    private Long participateCode;
}