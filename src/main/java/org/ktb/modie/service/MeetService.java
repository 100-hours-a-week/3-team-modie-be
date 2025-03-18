package org.ktb.modie.service;

import java.util.ArrayList;
import java.util.List;

import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.domain.Meet;
import org.ktb.modie.presentation.v1.dto.CreateMeetRequest;
import org.ktb.modie.presentation.v1.dto.CreateMeetResponse;
import org.ktb.modie.presentation.v1.dto.MeetDto;
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

    public MeetDto getMeet(long meetId) {
        // NOTE: 비정상적인 meetID가 넘어온 경우
        if (meetId <= 0) {
            throw new BusinessException(CustomErrorCode.INVALID_INPUT_IN_MEET);
        }
        // NOTE: 정상적인 meetID 이지만 Data가 없는 경우
        Meet meet = meetRepository.findById(meetId)
            .orElseThrow(() -> new BusinessException(
                CustomErrorCode.MEETING_NOT_FOUND
            ));
        List<Object> members = new ArrayList<>(); // 모임 참여 API 개발 뒤 추가예정

        MeetDto response = MeetDto.builder()
            .meetId(meet.getMeetId())
            .ownerName(meet.getOwner().getUserName())
            .meetIntro(meet.getMeetIntro())
            .meetType(meet.getMeetType())
            .address(meet.getAddress())
            .addressDescription(meet.getAddressDescription())
            .meetAt(meet.getMeetAt())
            .totalCost(meet.getTotalCost())
            .memberLimit(meet.getMemberLimit())
            .createdAt(meet.getCreatedAt())
            .meetRule("owner")
            .members(members)
            .build();

        return response;
    }
}
