package org.ktb.modie.repository;

import org.ktb.modie.domain.UserMeet;
import org.ktb.modie.presentation.v1.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserMeetRepository extends JpaRepository<UserMeet, Long> {

    @Query("SELECT new org.ktb.modie.presentation.v1.dto.UserDto(u.userId, u.userName, um.isPayed) "
        + "FROM UserMeet um JOIN um.user u "
        + "WHERE um.meet.meetId = :meetId AND um.deletedAt IS NULL")
    List<UserDto> findUserDtosByMeetId(@Param("meetId") Long meetId);

}
