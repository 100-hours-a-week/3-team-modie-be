package org.ktb.modie.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.core.util.HashIdUtil;
import org.ktb.modie.domain.Meet;
import org.ktb.modie.domain.User;
import org.ktb.modie.domain.UserMeet;
import org.ktb.modie.presentation.v1.dto.CreateMeetRequest;
import org.ktb.modie.presentation.v1.dto.CreateMeetResponse;
import org.ktb.modie.presentation.v1.dto.MeetDto;
import org.ktb.modie.presentation.v1.dto.MeetListResponse;
import org.ktb.modie.presentation.v1.dto.MeetSummaryDto;
import org.ktb.modie.presentation.v1.dto.UpdateMeetRequest;
import org.ktb.modie.presentation.v1.dto.UpdateMeetResponse;
import org.ktb.modie.presentation.v1.dto.UpdatePaymentRequest;
import org.ktb.modie.presentation.v1.dto.UserDto;
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

// meetService
@Service
@RequiredArgsConstructor
public class MeetService {

    private final UserMeetRepository userMeetRepository;
    private final UserRepository userRepository;
    private final MeetRepository meetRepository;
    private final HashIdUtil hashIdUtil;

    @Transactional
    public CreateMeetResponse createMeet(String userId, CreateMeetRequest request) {
        User owner = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(CustomErrorCode.USER_NOT_FOUND));

        // meetAt이 1년 이상 이후일 경우 예외
        if (request.meetAt().isAfter(LocalDateTime.now().plusYears(1))) {
            throw new BusinessException(CustomErrorCode.INVALID_DATE_TOO_FAR);
        }

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
        String meetHashId = hashIdUtil.encode(savedMeet.getMeetId());

        return new CreateMeetResponse(meetHashId);
    }

    @Transactional
    public void createUserMeet(String userId, String meetHashId) {
        Long meetId = hashIdUtil.decode(meetHashId);

        // Token 받아오면 userId로 변환하는 과정 필요
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(CustomErrorCode.USER_NOT_FOUND));

        // 모임 조회
        Meet meet = meetRepository.findById(meetId)
            .orElseThrow(() -> new BusinessException(CustomErrorCode.MEETING_NOT_FOUND));

        // 삭제나 완료된 모임
        if (meet.getDeletedAt() != null || meet.getCompletedAt() != null) {
            throw new BusinessException(CustomErrorCode.DENIED_JOIN_ALREADY_ENDED);
        }

        // 방장은 참여 불가
        if (meet.getOwner().getUserId().equals(userId)) {
            throw new BusinessException(CustomErrorCode.OWNER_CANNOT_JOIN_MEET);
        }

        // 중복 참여 방지 및 탈퇴한 사용자 처리
        Optional<UserMeet> existUserMeet = userMeetRepository
            .findUserMeetByUser_UserIdAndMeet_MeetId(userId, meetId);

        if (existUserMeet.isPresent()) {
            UserMeet userMeet = existUserMeet.get();

            if (userMeet.getDeletedAt() == null) {
                // 이미 참여 중
                throw new BusinessException(CustomErrorCode.ALREADY_JOINED_MEET);
            } else {
                // 탈퇴한 이력 있음
                userMeet.setDeletedAt(null);
                userMeet.setPayed(false);
                return; // 복구 후 종료
            }
        }

        // 정원 초과 여부 체크
        int currentMemberCount = userMeetRepository.countByMeetAndDeletedAtIsNull(meet) + 1;
        if (currentMemberCount >= meet.getMemberLimit()) {
            throw new BusinessException(CustomErrorCode.MEETING_CAPACITY_FULL);
        }
        // 참여 정보 저장
        UserMeet userMeet = UserMeet.builder()
            .user(user)
            .meet(meet)
            .isPayed(false)
            .build();

        userMeetRepository.save(userMeet);
    }

    public MeetDto getMeet(String userId, String meetHashId) {
        // NOTE: 비정상적인 meetID가 넘어온 경우
        Long meetId = hashIdUtil.decode(meetHashId);

        if (meetId <= 0) {
            throw new BusinessException(CustomErrorCode.INVALID_INPUT_IN_MEET);
        }
        // NOTE: 정상적인 meetID 이지만 Data가 없는 경우
        Meet meet = meetRepository.findById(meetId)
            .orElseThrow(() -> new BusinessException(
                CustomErrorCode.MEETING_NOT_FOUND
            ));
        // NOTE: 역할(owner, member, guest)
        String meetRule = getMeetRole(userId, meet);

        // NOTE: 참여중인 멤버
        List<UserDto> members = userMeetRepository.findUserDtosByMeetId(meetId);

        return MeetDto.builder()
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
            .updatedAt(meet.getUpdatedAt())
            .deletedAt(meet.getDeletedAt())
            .completedAt(meet.getCompletedAt())
            .meetRule(meetRule)
            .members(members)
            .build();
    }

    public MeetListResponse getMeetList(String userId, String meetType, Boolean isCompleted, int page) {
        if (meetType != null && meetType.length() > 10) {
            throw new BusinessException(CustomErrorCode.INVALID_INPUT_PAGE); // meetType이 10자를 초과하면 예외 발생
        }

        // 페이지 번호 검증
        if (page < 1) {
            throw new BusinessException(CustomErrorCode.INVALID_INPUT_PAGE);
        }

        // 페이징 설정 (기본 페이지 크기 = 10)
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "meetAt"));

        // 필터링된 모임 리스트 조회
        Page<Meet> meetPage = meetRepository.findFilteredMeets(userId, meetType, isCompleted, pageable);

        // 총 페이지 수를 벗어난 경우
        if (page > (meetPage.getTotalElements() / 10) + 1) {
            throw new BusinessException(CustomErrorCode.INVALID_INPUT_PAGE);
        }

        // MeetSummaryDto로 변환
        List<MeetSummaryDto> meetSummaryList = meetPage.getContent().stream()
            .map(meet -> new MeetSummaryDto(
                hashIdUtil.encode(meet.getMeetId()),
                meet.getMeetIntro(),
                meet.getMeetType(),
                meet.getMeetAt(),
                meet.getAddress(),
                meet.getAddressDescription(),
                meet.getTotalCost() > 0, // 비용 여부 (0보다 크면 true)
                userMeetRepository.countByMeetAndDeletedAtIsNull(meet) + 1, // 현재 참여 인원 수
                meet.getMemberLimit(), // 최대 인원 수
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

    @Transactional
    public void deleteUserMeet(String userId, String meetHashId) {
        Long meetId = hashIdUtil.decode(meetHashId);
        // 모임 조회
        Meet meet = meetRepository.findById(meetId)
            .orElseThrow(() -> new BusinessException(CustomErrorCode.MEETING_NOT_FOUND));

        // 사용자 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(CustomErrorCode.USER_NOT_FOUND));

        // 현재 시간이 모임 시작 시간을 지나면 나갈 수 없음
        if (LocalDateTime.now().isAfter(meet.getMeetAt())) {
            throw new BusinessException(CustomErrorCode.MEETING_ALREADY_STARTED);
        }

        // 종료된 모임 확인 - 종료된 모임은 나갈 수 없음
        if (meet.getCompletedAt() != null) {
            throw new BusinessException(CustomErrorCode.MEETING_ALREADY_ENDED);
        }

        // 사용자가 해당 모임에 참여 중인지 확인
        UserMeet userMeet = userMeetRepository.findUserMeetByUser_UserIdAndMeet_MeetIdAndDeletedAtIsNull(userId, meetId)
            .orElseThrow(() -> new BusinessException(CustomErrorCode.PERMISSION_DENIED_NOT_MEMBER));

        if (userMeet.getDeletedAt() != null) {
            throw new BusinessException(CustomErrorCode.ALREADY_EXITED_MEET);
        }

        // 모임에서 나가기 처리
        userMeet.setDeletedAt(LocalDateTime.now());
    }

    @Transactional
    public UpdateMeetResponse updateMeet(String userId, String meetHashId, UpdateMeetRequest request) {
        Long meetId = hashIdUtil.decode(meetHashId);

        // NOTE: 비정상적인 meetID가 넘어온 경우
        if (meetId <= 0) {
            throw new BusinessException(CustomErrorCode.INVALID_INPUT_IN_MEET);
        }

        // NOTE: 정상적인 meetID 이지만 Data가 없는 경우
        Meet meet = meetRepository.findById(meetId)
            .orElseThrow(() -> new BusinessException(
                CustomErrorCode.MEETING_NOT_FOUND
            ));

        // NOTE: 요청한 유저의 id이 ownerId와 같은지 확인 -> 토큰 구현 후 구현예정
        if (!meet.getOwner().getUserId().equals(userId)) {
            throw new BusinessException(CustomErrorCode.UNAUTHORIZED_USER_NOT_OWNER);
        }
        // ✅ 현재 참여 인원 수 확인
        int currentMemberCount = userMeetRepository.countByMeet_MeetIdAndDeletedAtIsNull(meetId);

        // ❌ 최소 인원 제한 위반
        if (request.memberLimit() < 2) {
            throw new BusinessException(CustomErrorCode.MEMBER_LIMIT_TOO_LOW);
        }

        // ❌ 현재 인원보다 낮게 설정한 경우
        if (request.memberLimit() < currentMemberCount) {
            throw new BusinessException(CustomErrorCode.MEMBER_LIMIT_LESS_THAN_CURRENT);
        }
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

    @Transactional
    public void completeMeet(String userId, String meetHashId) {
        Long meetId = hashIdUtil.decode(meetHashId);

        // 모임 조회
        Meet meet = meetRepository.findById(meetId)
            .orElseThrow(() -> new BusinessException(CustomErrorCode.MEETING_NOT_FOUND));

        // 모임 생성자인지 확인
        if (!meet.getOwner().getUserId().equals(userId)) {
            throw new BusinessException(CustomErrorCode.PERMISSION_DENIED_COMPLETED_NOT_OWNER);
        }

        // NOTE: 비용이 없는 경우 바로 종료
        if (meet.getTotalCost() == 0) {
            meet.setCompletedAt(LocalDateTime.now());
            return;
        }
        // NOTE: 정산 완료 여부 확인(비용이 있는 경우)
        Long unpaidUsers = userMeetRepository.countUnpaidActiveUsersByMeetId(meetId);
        if (unpaidUsers > 0) {
            throw new BusinessException(CustomErrorCode.OPERATION_DENIED_SETTLEMENT_INCOMPLETE);
        }

        // 모임 종료 처리
        meet.setCompletedAt(LocalDateTime.now());
    }

    @Transactional
    public void updatePaymentStatus(String userId, String meetHashId, UpdatePaymentRequest request) {
        Long meetId = hashIdUtil.decode(meetHashId);

        // 모임 조회
        Meet meet = meetRepository.findById(meetId)
            .orElseThrow(() -> new BusinessException(CustomErrorCode.MEETING_NOT_FOUND));

        // 종료된 모임이면 예외처리
        if (meet.getCompletedAt() != null && meet.getCompletedAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(CustomErrorCode.ALREADY_COMPLETED_MEET);
        }

        // 타겟 사용자 조회
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new BusinessException(CustomErrorCode.USER_NOT_FOUND));

        // 방장 사용자 조회
        User owner = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(CustomErrorCode.USER_NOT_FOUND));

        // 요청한 사람이 방장인지 확인
        if (!meet.getOwner().getUserId().equals(userId)) {
            throw new BusinessException(CustomErrorCode.PERMISSION_DENIED_SETTLEMENT_NOT_OWNER);
        }

        // 해당 유저가 해당 모임에 참여 중인지 확인
        UserMeet userMeet = userMeetRepository.findUserMeetByUser_UserIdAndMeet_MeetIdAndDeletedAtIsNull(
                request.userId(), meetId)
            .orElseThrow(() -> new BusinessException(CustomErrorCode.SETTLEMENT_PERMISSION_DENIED_NOT_MEMBER));

        // 정산 상태 변경 (true <-> false 토글)
        userMeet.setPayed(!userMeet.isPayed());
    }

    @Transactional
    public void deleteMeet(String meetHashId, String userId) {
        Long meetId = hashIdUtil.decode(meetHashId);

        // 모임 존재여부
        Meet meet = meetRepository.findActiveByMeedId(meetId)
            .orElseThrow(() -> new BusinessException(CustomErrorCode.MEETING_NOT_FOUND));

        // 모임 생성자 여부
        if (!meet.getOwner().getUserId().equals(userId)) { // 12345 -> currentUser(controller)
            throw new BusinessException(CustomErrorCode.PERMISSION_DENIED_NOT_OWNER);
        }
        // 시작된 모임 삭제 불가
        if (meet.getMeetAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(CustomErrorCode.MEETING_ALREADY_STARTED);
        }

        // soft delete
        meet.delete();
    }

    @Transactional
    public void updateTotalCost(String userId, String meetHashId, int totalCost) {
        Long meetId = hashIdUtil.decode(meetHashId);
        // 비정상적인 meetID가 넘어온 경우
        if (meetId <= 0) {
            throw new BusinessException(CustomErrorCode.INVALID_INPUT_IN_MEET);
        }
        // 모임 존재여부
        Meet meet = meetRepository.findActiveByMeedId(meetId)
            .orElseThrow(() -> new BusinessException(CustomErrorCode.MEETING_NOT_FOUND));

        // 모임 생성자 여부
        if (!meet.getOwner().getUserId().equals(userId)) {
            throw new BusinessException(CustomErrorCode.PERMISSION_DENIED_NOT_OWNER);
        }

        // 금액 유효성 검사
        if (totalCost < 0 || totalCost > 10000000) {
            throw new BusinessException(CustomErrorCode.INVALID_INPUT_IN_MEET);
        }

        meet.setTotalCost(totalCost);
    }

    private String getMeetRole(String userId, Meet meet) {
        // NOTE: 유저의 확인
        if (userId == null || userId.isEmpty() || meet == null) {
            return "guest"; // userId가 없는 경우 게스트
        }

        // NOTE: 모임의 소유자 확인
        if (meet.getOwner() != null && userId.equals(meet.getOwner().getUserId())) {
            return "owner"; // 모임의 소유자
        }

        // NOTE: 유저가 해당 모임에 존재하는지 확인 (삭제된 기록도 고려)
        Optional<UserMeet> userMeetOpt = userMeetRepository.findUserMeetByUser_UserIdAndMeet_MeetId(userId,
            meet.getMeetId());

        if (!userMeetOpt.isPresent()) {
            return "guest"; // 유저가 모임에 존재하지 않으면 게스트
        }

        UserMeet userMeet = userMeetOpt.get();

        // NOTE: deletedAt이 null이 아니면 탈퇴한 상태 -> guest
        if (userMeet.getDeletedAt() != null) {
            return "guest"; // 탈퇴한 유저는 게스트
        }

        return "member"; // 탈퇴하지 않은 유저는 멤버
    }
}
