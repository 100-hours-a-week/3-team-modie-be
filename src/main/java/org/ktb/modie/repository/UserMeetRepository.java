package org.ktb.modie.repository;

import org.ktb.modie.domain.UserMeet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMeetRepository extends JpaRepository<UserMeet, Long> {
    boolean isExistsByUserAndMeet(String userId, Long meetId);
}
