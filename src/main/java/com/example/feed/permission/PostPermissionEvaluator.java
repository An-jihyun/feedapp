package com.example.feed.permission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("postPermissionEvaluator")
@RequiredArgsConstructor
public class PostPermissionEvaluator {

    private final PostRepository postRepository;

    public boolean isOwner(Long postId, String email) {
        return postRepository.findById(postId)
                .map(post -> post.getUser().getEmail().equals(email))
                .orElse(false);
    }
}