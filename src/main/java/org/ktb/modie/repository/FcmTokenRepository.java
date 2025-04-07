package org.ktb.modie.repository;

import java.util.List;
import java.util.Optional;

import org.ktb.modie.domain.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Integer> {
    Optional<FcmToken> findByUser_UserId(String userId);

    List<FcmToken> findByUser_UserIdIn(List<String> userId);

    Optional<FcmToken> findByDeviceId(String deviceId);
}
