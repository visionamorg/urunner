package com.runhub.running.repository;

import com.runhub.running.model.RunningActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<RunningActivity, Long> {

    List<RunningActivity> findByUserIdOrderByActivityDateDesc(Long userId);

    boolean existsByExternalId(String externalId);

    List<RunningActivity> findAllByOrderByActivityDateDesc();

    @Query("SELECT COALESCE(SUM(a.distanceKm), 0) FROM RunningActivity a WHERE a.user.id = :userId")
    Double sumDistanceByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(a.distanceKm), 0) FROM RunningActivity a WHERE a.user.id = :userId AND a.activityDate >= :from AND a.activityDate <= :to")
    Double sumDistanceByUserIdAndDateRange(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT COUNT(a) FROM RunningActivity a WHERE a.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(a.durationMinutes), 0) FROM RunningActivity a WHERE a.user.id = :userId")
    Long sumDurationByUserId(@Param("userId") Long userId);

    @Query("SELECT a.user.id, a.user.username, a.user.firstName, a.user.lastName, a.user.profileImageUrl, " +
           "SUM(a.distanceKm) as totalDist, COUNT(a) as totalRuns " +
           "FROM RunningActivity a WHERE a.activityDate >= :from AND a.activityDate <= :to " +
           "GROUP BY a.user.id, a.user.username, a.user.firstName, a.user.lastName, a.user.profileImageUrl " +
           "ORDER BY totalDist DESC")
    List<Object[]> findRankingsByDateRange(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT a.user.id, a.user.username, a.user.firstName, a.user.lastName, a.user.profileImageUrl, " +
           "SUM(a.distanceKm) as totalDist, COUNT(a) as totalRuns " +
           "FROM RunningActivity a " +
           "GROUP BY a.user.id, a.user.username, a.user.firstName, a.user.lastName, a.user.profileImageUrl " +
           "ORDER BY totalDist DESC")
    List<Object[]> findAllTimeRankings();

    @Query("SELECT a.user.id, a.user.username, a.user.firstName, a.user.lastName, a.user.profileImageUrl, " +
           "SUM(a.distanceKm) as totalDist, COUNT(a) as totalRuns " +
           "FROM RunningActivity a WHERE a.user.id IN :userIds " +
           "GROUP BY a.user.id, a.user.username, a.user.firstName, a.user.lastName, a.user.profileImageUrl " +
           "ORDER BY totalDist DESC")
    List<Object[]> findRankingsByUserIds(@Param("userIds") List<Long> userIds);
}
