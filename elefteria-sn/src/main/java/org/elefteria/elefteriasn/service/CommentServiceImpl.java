package org.elefteria.elefteriasn.service;

import org.elefteria.elefteriasn.dao.CommentRepository;
import org.elefteria.elefteriasn.dao.PostRepository;
import org.elefteria.elefteriasn.dao.UserRepository;
import org.elefteria.elefteriasn.dto.CommentDto;
import org.elefteria.elefteriasn.entity.Comment;
import org.elefteria.elefteriasn.entity.Post;
import org.elefteria.elefteriasn.entity.User;
import org.elefteria.elefteriasn.exception.MyEntityNotFoundException;
import org.elefteria.elefteriasn.response.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService{

    private PostRepository postRepository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private NotificationService notificationService;

    @Autowired
    public CommentServiceImpl(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository, NotificationService notificationService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedDateDesc(postId);

        if(comments == null)
            throw new MyEntityNotFoundException("Not found comments for post: " + postId);

        List<CommentDto> commentDtos = comments.stream().map(comment -> new CommentDto(comment)).collect(Collectors.toList());

        return commentDtos;
    }

    @Override
    @Transactional
    public ResponseEntity<SuccessResponse> createComment(Long postId, CommentDto commentDto) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if(postOptional.isEmpty())
            throw new MyEntityNotFoundException("Post not found with id: " + postId);

        Post post = postOptional.get();

        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();

        Comment comment = new Comment(commentDto.getText());

        comment.setPost(post);
        comment.setUser(user);

        commentRepository.save(comment);

        notificationService.createCommentNotification(user, post, comment);

        SuccessResponse successResponse = new SuccessResponse(
                HttpStatus.OK.value(),
                "Comment created successfully",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }
}













