package com.example.feed.comment;

import com.example.feed.common.BaseEntity;
import com.example.feed.post.Post;
import com.example.feed.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JPA(Hibernate)가 리플렉션으로 객체를 생성할 수 있도록 기본 생성자를 제공합니다.
 * 일반 코드에서는 직접 new 하지 말고 Comment.create(...) 팩토리 메서드를 사용하세요.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "comments")
public class Comment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 255)
	private String content;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "post_id")
	private Post post;

	private Comment(String content, User user, Post post) {
		this.content = content;
		this.user = user;
		this.post = post;
	}

	public static Comment create(String content, User user, Post post) {
		return new Comment(content, user, post);
	}

	public void updateContent(String content) {
		this.content = content;
	}

}