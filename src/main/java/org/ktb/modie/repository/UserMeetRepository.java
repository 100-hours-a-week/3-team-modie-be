package org.ktb.modie.repository;

import org.ktb.modie.domain.Meet;
import org.ktb.modie.domain.User;
import org.ktb.modie.domain.UserMeet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMeetRepository extends JpaRepository<UserMeet, Long> {
    boolean existsByUserAndMeet(User user, Meet meet);
}
