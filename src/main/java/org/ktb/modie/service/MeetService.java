package org.ktb.modie.service;

import java.util.List;

import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.domain.Meet;
import org.ktb.modie.domain.User;
import org.ktb.modie.domain.UserMeet;
import org.ktb.modie.presentation.v1.dto.CreateMeetRequest;
import org.ktb.modie.presentation.v1.dto.CreateMeetResponse;
import org.ktb.modie.presentation.v1.dto.MeetListResponse;
import org.ktb.modie.presentation.v1.dto.MeetSummaryDto;
import org.ktb.modie.repository.MeetRepository;
import org.ktb.modie.repository.UserMeetRepository;
import org.ktb.modie.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

		Meet meet = meetRepository.findById(meetId)
			.orElseThrow(() -> new BusinessException(CustomErrorCode.MEETING_NOT_FOUND));

		// 중복 참여 방지
		if (userMeetRepository.existsByUserAndMeet(user, meet)) {
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

	public MeetListResponse getMeetList(String meetType, Boolean isCompleted, int page) {
		System.out.println("🔹 meetType: " + meetType);
		System.out.println("🔹 isCompleted: " + isCompleted);
		System.out.println("🔹 Pageable: " + page);
		if (meetType != null && meetType.length() > 10) {
			throw new BusinessException(CustomErrorCode.INVALID_INPUT_PAGE); // meetType이 10자를 초과하면 예외 발생
		}

		// 페이징 설정 (기본 페이지 크기 = 10)
		Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "meetAt"));

		// 필터링된 모임 리스트 조회
		Page<Meet> meetPage = meetRepository.findFilteredMeets(meetType, isCompleted, pageable);

		if (page < 1 || page > (meetPage.getTotalElements() / 10) + 1)
			throw new BusinessException(CustomErrorCode.INVALID_INPUT_PAGE); // 페이지 유효성 검사

		// MeetSummaryDto로 변환
		List<MeetSummaryDto> meetSummaryList = meetPage.getContent().stream()
			.map(meet -> new MeetSummaryDto(
				meet.getMeetId(),
				meet.getMeetIntro(),
				meet.getMeetType(),
				meet.getMeetAt(),
				meet.getAddress(),
				meet.getAddressDescription(),
				meet.getTotalCost() > 0, // 비용 여부 (0보다 크면 true)
				userMeetRepository.countByMeet(meet), // 현재 참여 인원 수
				meet.getMemberLimit().intValue(), // 최대 인원 수
				meet.getOwner().getUserName() // 모임장 이름
			))
			.toList();

		return new MeetListResponse(
			page,                      // 페이지 번호
			10,                        // 페이지 크기 (고정값)
			meetPage.getTotalElements(), // 전체 요소 수
			meetSummaryList             // 변환된 모임 리스트
		);
	}
}
