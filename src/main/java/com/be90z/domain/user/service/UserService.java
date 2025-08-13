package com.be90z.domain.user.service;

import com.be90z.domain.user.dto.request.UserMypageUpdateReqDTO;
import com.be90z.domain.user.dto.response.UserMypageResDTO;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserMypageResDTO getMypage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        return UserMypageResDTO.from(user.getNickname(), user.getEmail());
    }

    @Transactional
    public UserMypageResDTO updateMypage(Long userId, UserMypageUpdateReqDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        user.updateNickname(request.getUserName());
        userRepository.save(user);
        
        return UserMypageResDTO.from(user.getNickname(), user.getEmail());
    }
}