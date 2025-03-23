package org.ktb.modie.repository;

import org.ktb.modie.domain.Meet;
import org.ktb.modie.domain.UserMeet;
import org.ktb.modie.presentation.v1.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// userMeet
public interface UserMeetRepository extends JpaRepository<UserMeet, Long> {
    Optional<UserMeet> findUserMeetByUser_UserIdAndMeet_MeetId(String userId, Long meetId);

    int countByMeet(Meet meet);

    @Query("SELECT COUNT(um) FROM UserMeet um WHERE um.meet.meetId = :meetId AND um.isPayed = false")
    Long countUnpaidUsersByMeetId(@Param("meetId") Long meetId);

    @Query("SELECT new org.ktb.modie.presentation.v1.dto.UserDto(u.userId, u.userName, um.isPayed) "
        + "FROM UserMeet um JOIN um.user u "
        + "WHERE um.meet.meetId = :meetId AND um.deletedAt IS NULL")
    List<UserDto> findUserDtosByMeetId(@Param("meetId") Long meetId);
}
