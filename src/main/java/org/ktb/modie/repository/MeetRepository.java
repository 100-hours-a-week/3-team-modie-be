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

    // ✅ meetType과 isCompleted(완료시간 기준)를 기준으로 필터링 (+페이징 처리) - 최신
    @Query("SELECT m FROM Meet m WHERE "
        + "(:meetType = '전체' OR m.meetType = :meetType) "
        + "AND ((:isCompleted = false AND m.completedAt IS NULL) "
        + "OR (:isCompleted = true AND m.completedAt IS NOT NULL))"
        + "AND m.deletedAt IS NULL")
    Page<Meet> findFilteredMeets(@Param("meetType") String meetType,
        @Param("isCompleted") Boolean isCompleted,
        Pageable pageable);

    // soft delete
    @Query("SELECT m FROM Meet m WHERE m.meetId = :meetId AND m.deletedAt IS NULL")
    Optional<Meet> findActiveByMeedId(@Param("meetId") Long meetId);
}
