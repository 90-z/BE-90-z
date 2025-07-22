package com.be90z.domain.user.repository;

import com.be90z.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByProviderAndEmail(String provider, String email);
    
    Optional<User> findByNicknameAndEmail(String nickname, String email);
    
    boolean existsByEmail(String email);
}
