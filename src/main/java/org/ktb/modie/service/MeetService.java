package org.ktb.modie.service;

import java.time.LocalDateTime;

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
    public CreateMeetResponse createMeet(CreateMeetRequest request) {
        User user = User.builder()
            .userId("3966242908")
            .createdAt(LocalDateTime.now())
            .profileImageUrl("http://k.kakaocdn.net/dn/FDstZ/btsMylYzxgF/5j3m3aiBpxQfYe7avDR0RK/img_640x640.jpg")
            .userName("제이드")
            .build();
        //
        // User owner = userRepository.findById(userId)
        //     .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Meet meet = Meet.builder()
            .meetIntro(request.meetIntro())
            .meetType(request.meetType())
            .address(request.address())
            .addressDescription(request.addressDescription())
            .meetAt(request.meetAt())
            .totalCost(request.totalCost())
            .memberLimit(request.memberLimit())
            .owner(user)
            .build();

        Meet savedMeet = meetRepository.save(meet);

        return new CreateMeetResponse(savedMeet.getMeetId());
    }

    @Transactional
    public void joinMeet(Long meetId) {
        User user = User.builder()
            .userId("3966242908")
            .profileImageUrl("http://k.kakaocdn.net/dn/FDstZ/btsMylYzxgF/5j3m3aiBpxQfYe7avDR0RK/img_640x640.jpg")
            .userName("제이드")
            .build();
        //
        // User user = userRepository.findById(userId)
        //     .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Meet meet = meetRepository.findById(1L)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));

        // 중복 참여 방지
        if (userMeetRepository.existsByUserAndMeet(user, meet)) {
            throw new IllegalStateException("이미 참여한 모임입니다.");
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
