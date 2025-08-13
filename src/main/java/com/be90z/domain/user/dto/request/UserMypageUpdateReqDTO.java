package com.be90z.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserMypageUpdateReqDTO {
    private String userName;
    
    public UserMypageUpdateReqDTO(String userName) {
        this.userName = userName;
    }
}