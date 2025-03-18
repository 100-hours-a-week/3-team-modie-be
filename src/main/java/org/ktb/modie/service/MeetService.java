package org.ktb.modie.service;

import org.ktb.modie.domain.Meet;
import org.ktb.modie.presentation.dto.CreateMeetRequestDto;
import org.ktb.modie.presentation.dto.CreateMeetResponseDto;
import org.ktb.modie.repository.MeetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetService {

    private final MeetRepository meetRepository;

    @Transactional
    public CreateMeetResponseDto createMeet(CreateMeetRequestDto request) {
        Meet meet = Meet.builder()
            .meetIntro(request.meetIntro())
            .meetType(request.meetType())
            .address(request.address())
            .addressDescription(request.addressDescription())
            .meetAt(request.meetAt())
            .totalCost(request.totalCost())
            .memberLimit(request.memberLimit())
            .ownerId(3966242908L)
            .build();

        Meet savedMeet = meetRepository.save(meet);

        return new CreateMeetResponseDto(savedMeet.getMeetId());
    }
}
