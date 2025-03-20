package org.ktb.modie.repository;

import java.util.Optional;

import org.ktb.modie.domain.Meet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetRepository extends JpaRepository<Meet, Long> {
    // soft delete
    @Query("SELECT m FROM Meet m WHERE m.meetId = :meetId AND m.deletedAt IS NULL")
    Optional<Meet> findActiveByMeedId(@Param("meetId") Long meetId);
}
