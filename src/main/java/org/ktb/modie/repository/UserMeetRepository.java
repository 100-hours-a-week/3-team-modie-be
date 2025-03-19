package org.ktb.modie.repository;

import org.ktb.modie.domain.Meet;
import org.ktb.modie.domain.User;
import org.ktb.modie.domain.UserMeet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserMeetRepository extends JpaRepository<UserMeet, Long> {
    boolean existsByUserAndMeet(User user, Meet meet);

    int countByMeet(Meet meet);

    @Query("SELECT COUNT(um) FROM UserMeet um WHERE um.meet.meetId = :meetId AND um.isPayed = false")
    Long countUnpaidUsersByMeetId(@Param("meetId") Long meetId);
}
