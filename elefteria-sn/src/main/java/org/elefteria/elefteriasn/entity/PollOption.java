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
@Table(name = "poll_option")
public class PollOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`option`")
    private String option;

    private int number;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "poll_option_user",
                joinColumns = @JoinColumn(name = "poll_option_id"),
                inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> votes = new HashSet<>();

    public PollOption(){}

    public PollOption(String option, int number) {
        this.option = option;
        this.number = number;
    }

    public void addVote(User user){
        if(votes == null)
            votes = new HashSet<>();

        votes.add(user);
    }
}
