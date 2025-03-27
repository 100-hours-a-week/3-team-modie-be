package org.ktb.modie.repository;

import java.util.Optional;

import org.ktb.modie.domain.Meet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetRepository extends JpaRepository<Meet, Long> {
    Optional<Meet> findByMeetId(Long meetId);

    @Query("""
        SELECT DISTINCT m
        FROM Meet m
        LEFT JOIN UserMeet um ON um.meet = m AND um.user.userId = :userId AND um.deletedAt IS NULL
        WHERE (m.owner.userId = :userId OR um.user.userId = :userId)
        AND (:meetType = '전체'
        OR (:meetType = '기타' AND m.meetType NOT IN ('운동', '이동', '빨래'))
        OR m.meetType = :meetType)
        AND ((:isCompleted = false AND m.completedAt IS NULL) OR (:isCompleted = true AND m.completedAt IS NOT NULL))
        AND m.deletedAt IS NULL
        ORDER BY m.meetAt DESC
        """)
    Page<Meet> findFilteredMeets(
        @Param("userId") String userId,
        @Param("meetType") String meetType,
        @Param("isCompleted") Boolean isCompleted,
        Pageable pageable);

    // soft delete
    @Query("SELECT m FROM Meet m WHERE m.meetId = :meetId AND m.deletedAt IS NULL")
    Optional<Meet> findActiveByMeedId(@Param("meetId") Long meetId);
}
