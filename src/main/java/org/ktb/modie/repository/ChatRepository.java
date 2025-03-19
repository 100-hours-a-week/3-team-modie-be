package org.ktb.modie.repository;

import java.util.List;

import org.ktb.modie.domain.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

	// 최신 25개 채팅 내역을 불러오는 메서드 (JOIN 사용)
	@Query("SELECT c FROM Chat c " +
		"JOIN FETCH c.user u " +  // User 정보 JOIN
		"JOIN FETCH c.meet m " +  // Meet 정보 JOIN
		"WHERE m.meetId = :meetId " +
		"ORDER BY c.createdAt DESC")
	List<Chat> findTop25ByMeetIdOrderByCreatedAtDesc(Long meetId, Pageable pageable);

	// 특정 messageId보다 작은 메시지들을 불러오는 메서드 (JOIN 사용)
	@Query("SELECT c FROM Chat c " +
		"JOIN FETCH c.user u " +  // User 정보 JOIN
		"JOIN FETCH c.meet m " +  // Meet 정보 JOIN
		"WHERE m.meetId = :meetId AND c.messageId < :messageId " +
		"ORDER BY c.createdAt DESC")
	List<Chat> findByMeetIdAndMessageIdLessThanOrderByCreatedAtDesc(Long meetId, Long messageId, Pageable pageable);

}
