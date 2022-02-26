package org.elefteria.elefteriasn.controller;

import org.elefteria.elefteriasn.dto.CommentDto;
import org.elefteria.elefteriasn.response.SuccessResponse;
import org.elefteria.elefteriasn.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private CommentService commentService;


    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @GetMapping("/post/{postId}")
    public List<CommentDto> getCommentsForPost(@PathVariable("postId") Long postId){

        return commentService.getCommentsByPostId(postId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @PostMapping("/post/{postId}")
    public ResponseEntity<SuccessResponse> createComment(@PathVariable("postId") Long postId,
                                                         @RequestBody CommentDto commentDto){

        return commentService.createComment(postId, commentDto);
    }
}
