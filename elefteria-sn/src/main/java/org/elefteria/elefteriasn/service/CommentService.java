package org.elefteria.elefteriasn.service;

import org.elefteria.elefteriasn.dto.CommentDto;
import org.elefteria.elefteriasn.entity.Comment;
import org.elefteria.elefteriasn.response.SuccessResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CommentService {
    List<CommentDto> getCommentsByPostId(Long postId);

    ResponseEntity<SuccessResponse> createComment(Long postId, CommentDto commentDto);
}
