package com.example.feed.like;

import com.example.feed.common.BaseEntity;
import com.example.feed.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_likes",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"user_id", "target_type", "target_id"})
	})
public class Like extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "target_type", nullable = false)
	private LikeTargetType targetType;

	@Column(name = "target_id", nullable = false)
	private Long targetId;

	private Like(User user, LikeTargetType targetType, Long targetId) {
		this.user = user;
		this.targetType = targetType;
		this.targetId = targetId;
	}

	public static Like create(User user, LikeTargetType targetType, Long targetId) {
		return new Like(user, targetType, targetId);
	}

}