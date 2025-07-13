package com.be90z.domain.mission.repository;

import com.be90z.domain.mission.entity.Mission;
import com.be90z.domain.mission.entity.MissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
    
    List<Mission> findByMissionStatus(MissionStatus missionStatus);
    
    @Query("SELECT m FROM Mission m WHERE m.startDate <= :now AND m.endDate >= :now")
    List<Mission> findActiveMissions(@Param("now") LocalDateTime now);
    
    @Query("SELECT m FROM Mission m WHERE m.missionStatus = :status ORDER BY m.createdAt DESC")
    List<Mission> findByMissionStatusOrderByCreatedAtDesc(@Param("status") MissionStatus status);
}