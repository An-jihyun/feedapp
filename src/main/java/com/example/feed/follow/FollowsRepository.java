package com.example.feed.follow;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowsRepository extends JpaRepository<Follow, Long> {

	//팔로잉 목록 전체 조회
	List<Follow> findAllByFollowerIdAndDeletedFalse(Long followerId);

	//팔로워 목록 조회
	List<Follow> findByFollowingIdAndDeletedFalse(Long followingId);

	//단건 조회 및 예외처리
	Optional<Follow> findByFollowerIdAndFollowingIdAndDeletedFalse(Long followerId, Long followingId);

	//jh-탈퇴 시 유저가 연관된 모든 Follow 관계 (팔로워 or 팔로잉)
	@Query("SELECT f FROM Follow f WHERE (f.follower.id = :userId OR f.following.id = :userId) AND f.deleted = false")
	List<Follow> findAllByUserId(@Param("userId") Long userId);

}