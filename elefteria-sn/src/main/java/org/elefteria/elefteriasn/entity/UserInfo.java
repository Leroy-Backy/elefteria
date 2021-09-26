package org.elefteria.elefteriasn.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "user_info")
@Getter
@Setter
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String status;

    private int amountOfFollowers;

    private int amountOfFollows;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_id")
    private Image avatar;

    @CreationTimestamp
    private Date createdDate;

    @OneToOne(mappedBy = "userInfo")
    @JsonIgnore
    private User user;

    public UserInfo(){}

    public UserInfo(String firstName, String lastName, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
