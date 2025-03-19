package org.ktb.modie.service;

import java.time.LocalDateTime;

import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.domain.Meet;
import org.ktb.modie.presentation.v1.dto.CreateMeetRequest;
import org.ktb.modie.presentation.v1.dto.CreateMeetResponse;
import org.ktb.modie.presentation.v1.dto.UpdateMeetRequest;
import org.ktb.modie.presentation.v1.dto.UpdateMeetResponse;
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

    public UpdateMeetResponse updateMeet(Long meetId, UpdateMeetRequest request) {
        // NOTE: 비정상적인 meetID가 넘어온 경우
        if (meetId <= 0) {
            throw new BusinessException(CustomErrorCode.INVALID_INPUT_IN_MEET);
        }

        // NOTE: 정상적인 meetID 이지만 Data가 없는 경우
        Meet meet = meetRepository.findById(meetId)
            .orElseThrow(() -> new BusinessException(
                CustomErrorCode.MEETING_NOT_FOUND
            ));

        meet.setMeetIntro(request.meetIntro());
        meet.setMeetType(request.meetType());
        meet.setAddress(request.address());
        meet.setAddressDescription(request.addressDescription());
        meet.setMeetAt(request.meetAt());
        meet.setTotalCost(request.totalCost());
        meet.setMemberLimit(request.memberLimit());
        meet.setUpdatedAt(LocalDateTime.now());

        meetRepository.save(meet);

        return new UpdateMeetResponse(meet.getMeetId());
    }
}
