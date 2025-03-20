package org.ktb.modie.service;

import java.time.LocalDateTime;

import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.domain.Meet;
import org.ktb.modie.presentation.v1.dto.CreateMeetRequest;
import org.ktb.modie.presentation.v1.dto.CreateMeetResponse;
import org.ktb.modie.repository.MeetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetService {

    private final MeetRepository meetRepository;

    @Transactional
    public CreateMeetResponse createMeet(CreateMeetRequest request) {
        Meet meet = Meet.builder()
            .meetIntro(request.meetIntro())
            .meetType(request.meetType())
            .address(request.address())
            .addressDescription(request.addressDescription())
            .meetAt(request.meetAt())
            .totalCost(request.totalCost())
            .memberLimit(request.memberLimit())
            .owner(null)
            .build();

        Meet savedMeet = meetRepository.save(meet);

        return new CreateMeetResponse(savedMeet.getMeetId());
    }

    @Transactional
    public void deleteMeet(Long meetId) {
        // 모임 존재여부
        Meet meet = meetRepository.findActiveByMeedId(meetId)
            .orElseThrow(() -> new BusinessException(CustomErrorCode.MEETING_NOT_FOUND));

        // 모임 생성자 여부
        if (!meet.getOwner().getUserId().equals("12345")) { // 12345 -> currentUser(controller)
            throw new BusinessException(CustomErrorCode.PERMISSION_DENIED_NOT_OWNER);
        }
        // 시작된 모임 삭제 불가
        if (meet.getMeetAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(CustomErrorCode.MEETING_ALREADY_STARTED);
        }

        // soft delete
        meet.delete();
    }
}
