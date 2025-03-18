package org.ktb.modie.repository;

import java.util.List;

import org.ktb.modie.domain.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    // 최신 25개 채팅 내역을 불러오는 메서드
    List<Chat> findTop25ByMeetIdOrderByCreatedAtDesc(Long meetId);

    // 마지막 채팅 ID 기준으로 25개 채팅 내역을 불러오는 메서드
    List<Chat> findByMeetIdAndMessageIdLessThanOrderByCreatedAtDesc(Long meetId, Long messageId, Pageable pageable);
}
