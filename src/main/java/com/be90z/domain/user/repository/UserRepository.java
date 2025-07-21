package com.be90z.domain.user.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.be90z.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProvider(String provider);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
//    boolean existByProvider(String provider);
}
