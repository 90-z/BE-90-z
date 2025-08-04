package com.be90z.domain.mission.repository;

import com.be90z.domain.mission.entity.MissionReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MissionReplyRepository extends JpaRepository<MissionReply, Long> {
    
    @Query("SELECT mr FROM MissionReply mr " +
           "JOIN FETCH mr.user " +
           "WHERE mr.mission.missionCode = :missionCode " +
           "ORDER BY mr.createdAt DESC")
    List<MissionReply> findByMissionCodeOrderByCreatedAtDesc(@Param("missionCode") Long missionCode);
    
    @Query("SELECT mr FROM MissionReply mr " +
           "JOIN FETCH mr.mission " +
           "JOIN FETCH mr.user " +
           "WHERE mr.user.userId = :userId " +
           "ORDER BY mr.createdAt DESC")
    List<MissionReply> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}