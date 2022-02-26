package org.elefteria.elefteriasn.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
@Table(name = "user")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String email;

    private boolean isAccountNonExpired;

    private boolean isAccountNonLocked;

    private boolean isCredentialsNonExpired;

    private boolean isEnabled;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_info_id", referencedColumnName = "id")
    private UserInfo userInfo;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "following",
            joinColumns = @JoinColumn(name = "follower_user_id"),
            inverseJoinColumns = @JoinColumn(name = "followed_user_id"))
    private Set<User> follows = new HashSet<>();

    @ManyToMany(mappedBy = "follows", cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private Set<User> followers = new HashSet<>();

    public User(){
        this.isAccountNonExpired = true;
        this.isAccountNonLocked = true;
        this.isCredentialsNonExpired = true;
        this.isEnabled = true;
    }

    public User(String username, String password, String email, boolean isAccountNonExpired, boolean isAccountNonLocked, boolean isCredentialsNonExpired, boolean isEnabled, UserInfo userInfo, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isEnabled = isEnabled;
        this.userInfo = userInfo;
        this.roles = roles;
    }

    public void addFollow(User user){
        if(follows == null){
            follows = new HashSet<>();
        }
        follows.add(user);
        userInfo.setAmountOfFollows(userInfo.getAmountOfFollows() + 1);
        user.addFollower(this);
    }

    private void addFollower(User user){
        if(followers == null){
            followers = new HashSet<>();
        }
        followers.add(user);
        userInfo.setAmountOfFollowers(userInfo.getAmountOfFollowers() + 1);
    }

    public void removeFollow(User user){
        if(follows.contains(user)){
            follows.remove(user);
            userInfo.setAmountOfFollows(userInfo.getAmountOfFollows() - 1);
            user.removeFollower(this);
        }
    }

    private void removeFollower(User user){
        if(followers.contains(user)){
            followers.remove(user);
            userInfo.setAmountOfFollowers(userInfo.getAmountOfFollowers() - 1);
        }
    }

    public void addPost(Post post){
        if(posts == null)
            posts = new HashSet<>();

        posts.add(post);
        post.setUser(this);
    }

    public void addRole(Role role){
        if (roles == null)
            roles = new HashSet<>();

        roles.add(role);
    }

    public void removeRole(Role role){
        if(roles.contains(role)){
            roles.remove(role);
            role.removeUser(this);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", isAccountNonExpired=" + isAccountNonExpired +
                ", isAccountNonLocked=" + isAccountNonLocked +
                ", isCredentialsNonExpired=" + isCredentialsNonExpired +
                ", isEnabled=" + isEnabled +
                ", userInfo=" + userInfo +
                ", roles=" + roles +
                '}';
    }
}
