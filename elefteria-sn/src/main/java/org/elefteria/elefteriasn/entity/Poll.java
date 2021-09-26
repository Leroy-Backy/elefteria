package org.elefteria.elefteriasn.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@Table(name = "poll")
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id")
    private Set<PollOption> options = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    public Poll(){}

    public Poll(String question) {
        this.question = question;
    }

    public void addOption(PollOption option){
        if(options == null)
            options = new HashSet<>();

        options.add(option);
    }
}
