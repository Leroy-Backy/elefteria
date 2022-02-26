package org.elefteria.elefteriasn.controller;

import org.elefteria.elefteriasn.dto.PostDto;
import org.elefteria.elefteriasn.response.SuccessResponse;
import org.elefteria.elefteriasn.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @PostMapping("/{postId}/like")
    public ResponseEntity<SuccessResponse> likePost(@PathVariable("postId") Long postId){
        return postService.likePost(postId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @PostMapping()
    public ResponseEntity<SuccessResponse> createPost(@ModelAttribute PostDto postDto){
        return this.postService.createPost(postDto);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @GetMapping("/user/{userId}")
    public Page<PostDto> getPostsByUserId(@PathVariable("userId") Long userId,
                                          @RequestParam(required = false, name = "page", defaultValue = "0") Integer page,
                                          @RequestParam(required = false, name = "size", defaultValue = "10") Integer size){

        Pageable pageable = PageRequest.of(page, size);

        return this.postService.getPostsByUserId(userId, pageable);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @GetMapping("/feed")
    public Page<PostDto> getPostsFeed( @RequestParam(required = false, name = "page", defaultValue = "0") Integer page,
                                       @RequestParam(required = false, name = "size", defaultValue = "20") Integer size ){

        Pageable pageable = PageRequest.of(page, size);

        return postService.getPostsFeed(pageable);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable("postId") Long postId){
        return postService.getPostById(postId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @DeleteMapping("/{postId}")
    public ResponseEntity<SuccessResponse> deletePost(@PathVariable("postId") Long postId){
        return postService.deletePost(postId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @GetMapping("/popular")
    public Page<PostDto> getMostPopularPosts(@RequestParam(required = false, name = "page", defaultValue = "0") Integer page,
                                             @RequestParam(required = false, name = "size", defaultValue = "12") Integer size){

        Pageable pageable = PageRequest.of(page, size);

        return postService.getPopularPosts(pageable);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @GetMapping("/poll/{pollId}")
    public ResponseEntity<SuccessResponse> voteInPoll(@PathVariable("pollId") Long pollId, @RequestParam(required = false, name = "optionId", defaultValue = "1") Long optionId){
        return postService.voteInPoll(pollId, optionId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @GetMapping("/likes/{postId}")
    public Set<String> getLikesByPostId(@PathVariable("postId") Long postId){
        return postService.getLikesByPostId(postId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @GetMapping("/last")
    public PostDto getLastPostForCurrentUser(){
        return postService.getLastPostForCurrentUser();
    }
}
