package org.ktb.modie.repository;

import org.ktb.modie.domain.Meet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetRepository extends JpaRepository<Meet, Long> {

    // ✅ meetType과 isCompleted(완료시간 기준)를 기준으로 필터링 (+페이징 처리)
    @Query("SELECT m FROM Meet m WHERE "
        + "(:meetType IS NULL OR m.meetType = :meetType) "
        + "AND ((:isCompleted = false AND m.completedAt IS NULL) "
        + "OR (:isCompleted = true AND m.completedAt IS NOT NULL))")
    Page<Meet> findFilteredMeets(@Param("meetType") String meetType,
                                 @Param("isCompleted") Boolean isCompleted,
                                 Pageable pageable);
}
