package org.ktb.modie.service;

import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.domain.Meet;
import org.ktb.modie.domain.User;
import org.ktb.modie.domain.UserMeet;
import org.ktb.modie.presentation.v1.dto.CreateMeetRequest;
import org.ktb.modie.presentation.v1.dto.CreateMeetResponse;
import org.ktb.modie.repository.MeetRepository;
import org.ktb.modie.repository.UserMeetRepository;
import org.ktb.modie.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetService {

    private final UserMeetRepository userMeetRepository;
    private final UserRepository userRepository;
    private final MeetRepository meetRepository;

    @Transactional
    public CreateMeetResponse createMeet(String userId, CreateMeetRequest request) {
        User owner = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(CustomErrorCode.USER_NOT_FOUND));

        Meet meet = Meet.builder()
            .meetIntro(request.meetIntro())
            .meetType(request.meetType())
            .address(request.address())
            .addressDescription(request.addressDescription())
            .meetAt(request.meetAt())
            .totalCost(request.totalCost())
            .memberLimit(request.memberLimit())
            .owner(owner)
            .build();

        Meet savedMeet = meetRepository.save(meet);

        return new CreateMeetResponse(savedMeet.getMeetId());
    }

    @Transactional
    public void joinMeet(String userId, Long meetId) {
        // Token 받아오면 userId로 변환하는 과정 필요
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(CustomErrorCode.USER_NOT_FOUND));

        Meet meet = meetRepository.findById(5L)
            .orElseThrow(() -> new BusinessException(CustomErrorCode.MEETING_NOT_FOUND));

        // 모임 인원 초과 방지
        int currentMemberCount = userMeetRepository.countByMeet(meet.getMeetId());
        if (currentMemberCount >= meet.getMemberLimit()) {
            throw new BusinessException(CustomErrorCode.MEETING_CAPACITY_FULL);
        }

        // 중복 참여 방지
        if (userMeetRepository.isExistsByUserAndMeet(user.getUserId(), meet.getMeetId())) {
            throw new BusinessException(CustomErrorCode.ALREADY_JOINED_MEET);
        }

        // 참여 정보 저장
        UserMeet userMeet = UserMeet.builder()
            .user(user)
            .meet(meet)
            .isPayed(false)
            .build();

        userMeetRepository.save(userMeet);
    }
}
