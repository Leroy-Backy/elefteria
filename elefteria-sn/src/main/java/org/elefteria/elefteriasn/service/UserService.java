package org.elefteria.elefteriasn.service;

import org.elefteria.elefteriasn.dto.ChangePasswordDto;
import org.elefteria.elefteriasn.dto.RegisterDto;
import org.elefteria.elefteriasn.dto.SubscribedDto;
import org.elefteria.elefteriasn.dto.UserDto;
import org.elefteria.elefteriasn.response.SuccessResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface UserService {

    Page<UserDto> getFollowsByUserId(Long userId, Pageable pageable);

    Page<UserDto> getFollowersByUserId(Long userId, Pageable pageable);

    Page<UserDto> getUsers(Integer page, Integer size);

    UserDto getUser(String username);

    Page<UserDto> searchUserByName(String keyword, Pageable pageable);

    ResponseEntity<SuccessResponse> registerUser(RegisterDto registerDto);

    ResponseEntity<SuccessResponse> updateUser(UserDto userDto);

    ResponseEntity<SuccessResponse> followUser(Long userId);

    ResponseEntity<SuccessResponse> activateUser(String token);

    ResponseEntity<SuccessResponse> forgotPassword(String email);

    ResponseEntity<SuccessResponse> changePassword(ChangePasswordDto changePasswordDto);

    SubscribedDto isSubscribed(Long followedId);
}
