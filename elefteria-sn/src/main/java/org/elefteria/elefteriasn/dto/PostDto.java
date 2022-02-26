package org.elefteria.elefteriasn.dto;

import lombok.Data;
import org.elefteria.elefteriasn.entity.Image;
import org.elefteria.elefteriasn.entity.Like;
import org.elefteria.elefteriasn.entity.Post;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class PostDto {

    private Long id;

    private String title;

    private int amountOfLikes;

    private String text;

    private LocalDateTime createdDate;

    private String username;

    private Set<String> images;

    private MultipartFile[] files;

    private PollDto poll;

    private String pollString;

    private boolean liked = false;

    public PostDto(){}

    public PostDto(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.amountOfLikes = post.getAmountOfLikes();
        this.text = post.getText();
        this.createdDate = post.getCreatedDate();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Like> likeOptional = post.getLikes().stream().filter(like -> like.getUsername().equals(username)).findFirst();

        if(likeOptional.isPresent())
            this.liked = true;
        else
            this.liked = false;

        this.username = post.getUser().getUsername();
        this.files = null;
        this.images = post.getImages().stream().map(Image::getFileName).collect(Collectors.toSet());

        if(post.getPoll() != null)
            this.poll = new PollDto(post.getPoll());

    }

    public static PostDto formatToPostDto(Post post) {
        PostDto postDto = new PostDto(post);
        return postDto;
    }
}
