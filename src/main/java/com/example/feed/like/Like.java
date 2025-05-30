package com.example.feed.like;

import com.example.feed.common.BaseEntity;
import com.example.feed.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "likes",
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
