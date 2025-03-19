package org.ktb.modie.repository;

import java.util.Optional;

import org.ktb.modie.domain.Meet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetRepository extends JpaRepository<Meet, Long> {
    Optional<Meet> findByMeetId(Long meetId);
}
