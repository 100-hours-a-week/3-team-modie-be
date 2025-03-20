package org.ktb.modie.service;

import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.domain.User;
import org.ktb.modie.presentation.v1.dto.UserResponse;
import org.ktb.modie.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.ktb.modie.presentation.v1.dto.UpdateAccountRequest;
import org.ktb.modie.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void updateAccount(String userId, UpdateAccountRequest request) {
        // 사용자 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(CustomErrorCode.USER_NOT_FOUND));

        // 계좌 정보 업데이트
        user.setBankName(request.bankName());
        user.setAccountNumber(request.accountNumber());
    }
}
