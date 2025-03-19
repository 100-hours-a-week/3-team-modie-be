package org.ktb.modie.repository;

import java.util.Optional;

import org.ktb.modie.domain.Meet;
import org.ktb.modie.domain.UserMeet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserMeetRepository extends JpaRepository<UserMeet, Long> {
    Optional<UserMeet> findUserMeetByUser_UserIdAndMeet_MeetId(String userId, Long meetId);

    int countByMeet(Meet meet);

    @Query("SELECT COUNT(um) FROM UserMeet um WHERE um.meet.meetId = :meetId AND um.isPayed = false")
    Long countUnpaidUsersByMeetId(@Param("meetId") Long meetId);
}
