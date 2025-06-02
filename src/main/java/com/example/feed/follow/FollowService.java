package com.example.feed.follow;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.feed.exception.FollowNotFollowerException;
import com.example.feed.exception.FollowNotLoginException;
import com.example.feed.exception.FollowNotMySelfException;
import com.example.feed.exception.FollowerUserExistException;
import com.example.feed.exception.FollowsAlreadyFollowingException;
import com.example.feed.follow.dto.FollowingListResponseDto;
import com.example.feed.follow.dto.FollowsRequestDto;
import com.example.feed.follow.dto.FollowsResponseDto;
import com.example.feed.security.userDetail.CustomUserDetails;
import com.example.feed.user.User;
import com.example.feed.user.UserRepository;

@Service
@Transactional
public class FollowService {

	private final FollowsRepository followsRepository;
	private final UserRepository userRepository;

	public FollowService(FollowsRepository followsRepository, UserRepository userRepository) {
		this.followsRepository = followsRepository;
		this.userRepository = userRepository;
	}

	//팔로잉 추가
	public FollowsResponseDto follow(CustomUserDetails userDetails, FollowsRequestDto requestDto) {
		//로그인 사용자 Id
		Long followerId = userDetails.getUserId();

		//팔로잉 대상 Id
		Long followingId = requestDto.getUserId();

		//자기 자신 팔로우 금지 도메인 유틸 메서드로 활용
		if (followerId.equals(followingId)) {
			throw new FollowNotMySelfException("자신은 팔로우 할 수 없습니다.");
		}

		//팔로우 중복 금지 예외처리
		Optional<Follow> alreadyFollower = followsRepository.findByFollowerIdAndFollowingIdAndDeletedFalse(followerId,
			followingId);

		if (alreadyFollower.isPresent()) {
			throw new FollowsAlreadyFollowingException("이미 팔로우중인 사용자입니다.");
		}

		//유저 조회 도메인 유틸메서드로 활용
		User follower = userRepository.findById(followerId)
			.orElseThrow(() -> new FollowNotLoginException("로그인 후 이용해 주세요"));

		User following = userRepository.findById(followingId)
			.orElseThrow(() -> new FollowerUserExistException("팔로우 대상이 없습니다."));

		//follow 객체 생성
		Follow follow = Follow.of(follower, following);
		followsRepository.save(follow);
		return FollowsResponseDto.from(follow);
	}

	//팔로잉 목록 전체 조회
	@Transactional(readOnly = true)
	public List<FollowingListResponseDto> getFollowings(CustomUserDetails userDetails) {
		Long userId = userDetails.getUserId();

		return followsRepository.findAllByFollowerIdAndDeletedFalse(userId).stream()
			.map(follow -> FollowingListResponseDto.from(follow.getFollowing())).toList();
	}

	//팔로잉 단건 조회
	@Transactional(readOnly = true)
	public FollowsResponseDto getFollowing(CustomUserDetails userDetails, Long followingId) {
		Long userId = userDetails.getUserId();

		//팔로잉 여부 유틸 메서드로 활용
		Follow follow = followsRepository.findByFollowerIdAndFollowingIdAndDeletedFalse(userId, followingId)
			.orElseThrow(() -> new FollowNotFollowerException("팔로우중이아닙니다."));

		return FollowsResponseDto.from(follow);
	}

	//팔로워 목록 전체 조회
	@Transactional(readOnly = true)
	public List<FollowingListResponseDto> getFollowers(CustomUserDetails userDetails) {
		Long userId = userDetails.getUserId();
		return followsRepository.findByFollowingIdAndDeletedFalse(userId).stream()
			.map(follow -> FollowingListResponseDto.from(follow.getFollower())).toList();
	}

	//팔로워 단건 조회
	@Transactional(readOnly = true)
	public FollowsResponseDto getFollower(CustomUserDetails userDetails, Long followerId) {
		Long userId = userDetails.getUserId();
		//팔로워 여부 유틸 메서드로 활용
		Follow follow = followsRepository.findByFollowerIdAndFollowingIdAndDeletedFalse(followerId, userId)
			.orElseThrow(() -> new FollowNotFollowerException("팔로워가 아닙니다."));

		return FollowsResponseDto.fromFollower(follow);
	}

	//팔로우 삭제
	@Transactional
	public void softDeleteFollow(CustomUserDetails userDetails, String username) {
		//로그인 사용자
		Long userId = userDetails.getUserId();

		//상대방 유저 조회
		User followingUser = userRepository.findByUserNameAndDeletedFalse(username)
			.orElseThrow(() -> new FollowerUserExistException("유저를 찾을 수 없습니다."));

		//팔로우 관계 여부 유틸 메서드로 활용
		Follow follow = followsRepository.findByFollowerIdAndFollowingIdAndDeletedFalse(userId, followingUser.getId())
			.orElseThrow(() -> new FollowNotFollowerException("팔로우 중이 아닙니다."));
		follow.softDelete();
	}

	//jh-user삭제시 연관 팔로우 삭제
	@Transactional
	public void softDeleteFollowsByUserId(Long userId) {
		List<Follow> follows = followsRepository.findAllByUserId(userId);

		// 아래 설명
		for (Follow follow : follows) {
			follow.softDelete();
		}
	}

}