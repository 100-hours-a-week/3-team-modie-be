package org.ktb.modie.repository;

import java.util.List;

import org.ktb.modie.domain.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    // 최신 25개 채팅 내역을 불러오는 메서드 (JOIN 사용)
    @Query("SELECT c FROM Chat c "
        + "JOIN FETCH c.user u "
        + "JOIN FETCH c.meet m "
        + "WHERE m.meetId = :meetId "
        + "ORDER BY c.createdAt DESC")
    List<Chat> findTop25ByMeetIdOrderByCreatedAtDesc(
        @Param("meetId") Long meetId, Pageable pageable);

    @Query("SELECT c FROM Chat c "
        + "JOIN FETCH c.user u "
        + "JOIN FETCH c.meet m "
        + "WHERE m.meetId = :meetId AND c.messageId < :messageId "
        + "ORDER BY c.createdAt DESC")
    List<Chat> findByMeetIdAndMessageIdLessThanOrderByCreatedAtDesc(
        @Param("meetId") Long meetId,
        @Param("messageId") Long messageId,
        Pageable pageable);
}
