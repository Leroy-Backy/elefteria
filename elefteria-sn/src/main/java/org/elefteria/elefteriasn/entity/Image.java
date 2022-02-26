package org.elefteria.elefteriasn.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Base64;

@Entity
@Table(name = "image")
@Getter
@Setter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Image(){}

    public Image(String fileName){this.fileName = fileName;}
}
