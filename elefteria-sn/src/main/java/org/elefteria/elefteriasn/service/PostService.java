package org.elefteria.elefteriasn.service;

import org.elefteria.elefteriasn.dto.PostDto;
import org.elefteria.elefteriasn.entity.Poll;
import org.elefteria.elefteriasn.response.SuccessResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface PostService {

    Page<PostDto> getPostsFeed(Pageable pageable);

    ResponseEntity<SuccessResponse> createPost(PostDto postDto);

    Page<PostDto> getPostsByUserId(Long id, Pageable pageable);

    ResponseEntity<SuccessResponse> likePost(Long postId);

    Page<PostDto> getPopularPosts(Pageable pageable);

    ResponseEntity<SuccessResponse> deletePost(Long postId);

    ResponseEntity<SuccessResponse> voteInPoll(Long pollId, Long optionId);

    Set<String> getLikesByPostId(Long postId);
    
    PostDto getLastPostForCurrentUser();
}
