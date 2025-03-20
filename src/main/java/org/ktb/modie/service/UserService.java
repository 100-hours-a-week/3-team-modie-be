package org.ktb.modie.service;

import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.domain.User;
import org.ktb.modie.presentation.v1.dto.UpdateAccountRequest;
import org.ktb.modie.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
