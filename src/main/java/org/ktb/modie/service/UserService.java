package org.ktb.modie.service;

import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.domain.User;
import org.ktb.modie.presentation.v1.dto.UserResponse;
import org.ktb.modie.repository.UserRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(
                CustomErrorCode.USER_NOT_FOUND
            ));

        // TODO: 인증되지 않은 사용자(토큰 유효성 검사) 예외처리

        return UserResponse.builder()
            .userId(user.getUserId())
            .userName(user.getUserName())
            .profileImageUrl(user.getProfileImageUrl())
            .bankName(user.getBankName())
            .accountNumber(user.getAccountNumber())
            .build();
    }
}
