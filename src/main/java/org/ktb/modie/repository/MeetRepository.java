package org.ktb.modie.repository;

import org.ktb.modie.domain.Meet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetRepository extends JpaRepository<Meet, Integer> {
    // 기본적인 CRUD 작업은 JpaRepository에서 제공
    Meet findByMeetId(Integer meetId);  // meetId로 모임 조회
}
