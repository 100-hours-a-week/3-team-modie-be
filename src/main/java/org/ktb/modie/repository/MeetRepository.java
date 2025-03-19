package org.ktb.modie.repository;

import java.util.Optional;

import org.ktb.modie.domain.Meet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetRepository extends JpaRepository<Meet, Long> {
    // 'meetId'로 동작하는 기본 메서드 사용
    Optional<Meet> findByMeetId(Long meetId);
}
