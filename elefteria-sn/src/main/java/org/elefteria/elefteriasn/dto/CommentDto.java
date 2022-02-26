package org.elefteria.elefteriasn.dto;

import lombok.Data;
import org.elefteria.elefteriasn.entity.Comment;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private Long id;

    private String text;

    private LocalDateTime createdDate;

    private String username;

    public CommentDto(){}

    public CommentDto(Long id, String text, LocalDateTime createdDate, String username) {
        this.id = id;
        this.text = text;
        this.createdDate = createdDate;
        this.username = username;
    }

    public CommentDto(Comment comment){
        this.id = comment.getId();
        this.text = comment.getText();
        this.createdDate = comment.getCreatedDate();
        this.username = comment.getUser().getUsername();
    }

    public static CommentDto formatToCommentDto(Comment comment){
        return new CommentDto(comment);
    }
}
