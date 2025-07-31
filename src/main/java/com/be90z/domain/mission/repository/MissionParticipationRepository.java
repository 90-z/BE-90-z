package com.be90z.domain.mission.repository;

import com.be90z.domain.mission.entity.MissionParticipation;
import com.be90z.domain.mission.entity.ParticipateStatus;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissionParticipationRepository extends JpaRepository<MissionParticipation, Long> {
    
    List<MissionParticipation> findByUser(User user);
    
    List<MissionParticipation> findByMission(Mission mission);
    
    List<MissionParticipation> findByUserAndParticipateStatus(User user, ParticipateStatus status);
    
    Optional<MissionParticipation> findByUserAndMission(User user, Mission mission);
    
    @Query("SELECT mp FROM MissionParticipation mp WHERE mp.user = :user AND mp.participateStatus = 'PART_COMPLETE'")
    List<MissionParticipation> findCompletedParticipationsByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(mp) FROM MissionParticipation mp WHERE mp.mission = :mission AND mp.participateStatus = 'PART_COMPLETE'")
    Long countCompletedParticipationsByMission(@Param("mission") Mission mission);
    
    @Query("SELECT COUNT(mp) FROM MissionParticipation mp WHERE mp.user.userId = :userId AND mp.participateStatus = :status")
    Integer countByUserIdAndParticipateStatus(@Param("userId") Long userId, @Param("status") ParticipateStatus status);
    
    void deleteByMission(Mission mission);
}