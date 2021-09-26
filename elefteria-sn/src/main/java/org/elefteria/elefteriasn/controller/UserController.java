package org.elefteria.elefteriasn.controller;

import org.elefteria.elefteriasn.dto.ChangePasswordDto;
import org.elefteria.elefteriasn.dto.RegisterDto;
import org.elefteria.elefteriasn.dto.SubscribedDto;
import org.elefteria.elefteriasn.dto.UserDto;
import org.elefteria.elefteriasn.response.SuccessResponse;
import org.elefteria.elefteriasn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;;import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @GetMapping("/search")
    public Page<UserDto> searchUsersByName(@RequestParam("keyword") String keyword,
                                           @RequestParam(required = false, name = "page") Integer page,
                                           @RequestParam(required = false, name = "size") Integer size){
        if(size == null)
            size = 20;
        if(page == null)
            page = 0;

        Pageable pageable = PageRequest.of(page, size);

        return userService.searchUserByName(keyword, pageable);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @GetMapping
    public Page<UserDto> getUsers(@RequestParam(required = false, name = "page") Integer page, @RequestParam(required = false, name = "size") Integer size){
        if(page == null)
            page = 0;

        if(size == null)
            size = 10;

        return userService.getUsers(page, size);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @GetMapping("/isSubscribed/{id}")
    public SubscribedDto isSubscribed(@PathVariable("id") Long id){
        return userService.isSubscribed(id);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    public ResponseEntity<SuccessResponse> updateUser(@RequestBody UserDto userDto){
        return this.userService.updateUser(userDto);
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    public UserDto getUser(@PathVariable("username") String username){

        return userService.getUser(username);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @GetMapping("/current/get")
    public UserDto getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userService.getUser(username);
    }


    @PostMapping()
    @PreAuthorize("permitAll()")
    public ResponseEntity<SuccessResponse> registerUser(@Valid @RequestBody RegisterDto registerDto,
                                                        BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            throw new RuntimeException("Invalid data");
        }

        return userService.registerUser(registerDto);
    }

    @GetMapping("/activateUser")
    @PreAuthorize("permitAll()")
    public ResponseEntity<SuccessResponse> activateUser(@RequestParam("token") String token){
        return userService.activateUser(token);
    }

    @PostMapping("/{userId}/follow")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    public ResponseEntity<SuccessResponse> followUser(@PathVariable("userId") Long userId){
        return this.userService.followUser(userId);
    }

    @GetMapping("/{userId}/followers")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    public Page<UserDto> getFollowersByUserId(@PathVariable("userId") Long userId,
                                              @RequestParam(required = false, name = "size") Integer size,
                                              @RequestParam(required = false, name = "page") Integer page){
        if(size == null)
            size = 30;
        if(page == null)
            page = 0;

        Pageable pageable = PageRequest.of(page, size);

        return userService.getFollowersByUserId(userId, pageable);
    }

    @GetMapping("/{userId}/follows")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    public Page<UserDto> getFollowsByUserId(@PathVariable("userId") Long userId,
                                              @RequestParam(required = false, name = "size") Integer size,
                                              @RequestParam(required = false, name = "page") Integer page){
        if(size == null)
            size = 30;
        if(page == null)
            page = 0;

        Pageable pageable = PageRequest.of(page, size);

        return userService.getFollowsByUserId(userId, pageable);
    }

    @GetMapping("/changePassword")
    @PreAuthorize("permitAll()")
    public ResponseEntity<SuccessResponse> forgotPassword(@RequestParam("email") String email){
        return userService.forgotPassword(email);
    }

    @PostMapping("/changePassword")
    @PreAuthorize("permitAll()")
    public ResponseEntity<SuccessResponse> changePassword(@RequestBody ChangePasswordDto changePasswordDto){
        return userService.changePassword(changePasswordDto);
    }
}




























