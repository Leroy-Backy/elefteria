package org.elefteria.elefteriasn.dto;

import lombok.Data;
import org.elefteria.elefteriasn.entity.Role;
import org.elefteria.elefteriasn.entity.User;
import org.elefteria.elefteriasn.entity.UserInfo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto {

    private Long id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String status;

    private String avatar;

    private int amountOfFollowers;

    private int amountOfFollows;

    private Set<Role> roles = new HashSet<>();

    public UserDto(){}

    public UserDto(User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.roles = user.getRoles();
        this.username = user.getUsername();

        this.firstName = user.getUserInfo().getFirstName();
        this.lastName = user.getUserInfo().getLastName();
        this.status = user.getUserInfo().getStatus();
        this.amountOfFollowers = user.getUserInfo().getAmountOfFollowers();
        this.amountOfFollows = user.getUserInfo().getAmountOfFollows();

        if(user.getUserInfo().getAvatar() != null){
            this.avatar = user.getUserInfo().getAvatar().getFileName();
        }


    }

    public UserDto(Long id, String email, String username, UserInfo userInfo, Set<Role> roles) {
        this.id = id;
        this.email = email;
        this.roles = roles;
        this.username = username;
        this.firstName = userInfo.getFirstName();
        this.lastName = userInfo.getLastName();
        this.status = userInfo.getStatus();

        this.amountOfFollowers = userInfo.getAmountOfFollowers();
        this.amountOfFollows = userInfo.getAmountOfFollows();

        if(userInfo.getAvatar() != null){
            this.avatar = userInfo.getAvatar().getFileName();
        }
    }

    public static UserDto formatToUserDto(User user){
        UserDto userDto = new UserDto(user);

        return userDto;
    }
}
