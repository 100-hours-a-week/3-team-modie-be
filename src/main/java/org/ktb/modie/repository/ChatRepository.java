package org.ktb.modie.repository;

import java.util.List;

import org.ktb.modie.domain.Chat;
import org.ktb.modie.domain.Meet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    List<Chat> findByMeet_MeetId(Integer meetId);  // meetId로 쿼리

    List<Chat> findByMeet(Meet meet);
}
