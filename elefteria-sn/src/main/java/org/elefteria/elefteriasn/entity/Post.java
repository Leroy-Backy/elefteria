package org.elefteria.elefteriasn.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

@Entity
@Table(name = "post")
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private int amountOfLikes;

    private String text;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<Like> likes = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Image> images = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "post")
    private Poll poll;

    public Post(){ }

    public Post(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public void addImage(Image image){
        if(this.images == null){
            this.images = new HashSet<>();
        }

        this.images.add(image);

        image.setPost(this);
    }

    public void addLike(Like like){
        if(this.likes == null){
            this.likes = new HashSet<>();
        }

        likes.add(like);
        like.setPost(this);
//        amountOfLikes++;
    }

    public void removeLike(Like like){
        if(this.likes.contains(like)) {
            this.likes.remove(like);
        }
        like.setPost(null);
//        amountOfLikes--;
    }
}
