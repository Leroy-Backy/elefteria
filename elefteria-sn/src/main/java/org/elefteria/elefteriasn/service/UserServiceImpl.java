package org.elefteria.elefteriasn.service;

import org.elefteria.elefteriasn.dao.RegistrationAndChangePasswordTokenRepository;
import org.elefteria.elefteriasn.dao.RoleRepository;
import org.elefteria.elefteriasn.dao.UserRepository;
import org.elefteria.elefteriasn.dto.ChangePasswordDto;
import org.elefteria.elefteriasn.dto.RegisterDto;
import org.elefteria.elefteriasn.dto.SubscribedDto;
import org.elefteria.elefteriasn.dto.UserDto;
import org.elefteria.elefteriasn.email.EmailSender;
import org.elefteria.elefteriasn.email.EmailSenderImpl;
import org.elefteria.elefteriasn.entity.RegistrationAndChangePasswordToken;
import org.elefteria.elefteriasn.entity.Role;
import org.elefteria.elefteriasn.entity.User;
import org.elefteria.elefteriasn.entity.UserInfo;
import org.elefteria.elefteriasn.exception.MyEntityNotFoundException;
import org.elefteria.elefteriasn.exception.MyForbiddenException;
import org.elefteria.elefteriasn.response.SuccessResponse;
import org.elefteria.elefteriasn.security.jwt.JwtConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    @Value("${mail.link}")
    private String mailLink;
    @Value("${change.password.link}")
    private String changePasswordLink;

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private RegistrationAndChangePasswordTokenRepository registrationAndChangePasswordTokenRepository;
    private NotificationService notificationService;

    private JwtConfig jwtConfig;
    private PasswordEncoder passwordEncoder;
    private EmailSender emailSender;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, RegistrationAndChangePasswordTokenRepository registrationAndChangePasswordTokenRepository, NotificationService notificationService, JwtConfig jwtConfig, PasswordEncoder passwordEncoder, EmailSender emailSender) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.registrationAndChangePasswordTokenRepository = registrationAndChangePasswordTokenRepository;
        this.notificationService = notificationService;
        this.jwtConfig = jwtConfig;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
    }

    @Override
    @Transactional
    public Page<UserDto> getFollowsByUserId(Long userId, Pageable pageable) {
        Page<User> usersPage = userRepository.findByFollowers_id(userId, pageable);

        Page<UserDto> userDtoPage = usersPage.map(UserDto::formatToUserDto);

        return userDtoPage;
    }

    @Override
    @Transactional
    public Page<UserDto> getFollowersByUserId(Long userId, Pageable pageable) {
        Page<User> userPage = userRepository.findByFollows_id(userId, pageable);

        Page<UserDto> userDtoPage = userPage.map(UserDto::formatToUserDto);

        return userDtoPage;
    }

    @Override
    @Transactional
    public Page<UserDto> getUsers(Integer page, Integer size) {

        Pageable pageable = PageRequest.of(page, size);

        // Get Page of users
        Page<User> users = userRepository.findUsersOrderByFollowers(pageable);

        // transform users to dto for sending
        Page<UserDto> userDtos = users.map(UserDto::formatToUserDto);

        return userDtos;
    }

    @Override
    @Transactional
    public UserDto getUser(String username) {

        Optional<User> userOptional = userRepository.findByUsername(username);

        if(userOptional.isEmpty())
            throw new MyEntityNotFoundException("User " + username + " not found");

        User user = userOptional.get();

        UserDto userDto = new UserDto(user);

        return userDto;
    }

    @Override
    @Transactional
    public Page<UserDto> searchUserByName(String keyword, Pageable pageable) {
        Page<User> userPage = userRepository.searchByUsernameOrFirstOrLastName(keyword, pageable);

        Page<UserDto> userDtoPage = userPage.map(UserDto::formatToUserDto);

        return userDtoPage;
    }

    @Override
    @Transactional
    public ResponseEntity<SuccessResponse> registerUser(RegisterDto registerDto) {

        // Check if there is user with same username or email
        Optional<User> userCheck = userRepository.findByUsername(registerDto.getUsername());
        Optional<User> emailCheck = userRepository.findByEmail(registerDto.getEmail());

        User user = null;

        if(emailCheck.isPresent() && !emailCheck.get().isEnabled()){
            user = emailCheck.get();
        } else if(emailCheck.isPresent()) {
            throw new RuntimeException("User with email: " + registerDto.getEmail() + " already exists");
        }

        if(userCheck.isPresent() && !userCheck.get().isEnabled()){
            if(user == null)
                user = userCheck.get();

        } else if(userCheck.isPresent()){
            throw new RuntimeException("User " + registerDto.getUsername() + " already exists");
        } else {
            user = new User();
        }

        // Build User from dto
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setEmail(registerDto.getEmail());

        UserInfo userInfo = new UserInfo();
        user.setUserInfo(userInfo);

        // Get USER_ROLE from DB
        Role role = roleRepository.findById(1L).get();

        user.addRole(role);
        user.setEnabled(false);

        userRepository.save(user);

        // generate confirmation token
        String token = UUID.randomUUID().toString();

        RegistrationAndChangePasswordToken registrationAndChangePasswordToken = new RegistrationAndChangePasswordToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(jwtConfig.getRegistrationTokenExpirationMinutes()),
                user
        );

        registrationAndChangePasswordTokenRepository.save(registrationAndChangePasswordToken);

        String link = mailLink + token;

        emailSender.send(registerDto.getEmail(), EmailSenderImpl.getEmailHtmlTemplate(registerDto.getUsername(), link), "Confirm your email");

        // Make Response to client
        SuccessResponse response = new SuccessResponse(
                HttpStatus.OK.value(),
                "Successfully Registered user: " + registerDto.getUsername(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    @Override
    @Transactional
    public ResponseEntity<SuccessResponse> activateUser(String token) {
        Optional<RegistrationAndChangePasswordToken> registrationTokenOptional =
                registrationAndChangePasswordTokenRepository.findByTokenAndChangePassword(token, false);

        if(registrationTokenOptional.isEmpty())
            throw new MyEntityNotFoundException("Registration token not found");

        LocalDateTime expiredAt = registrationTokenOptional.get().getExpiresAt();

        if(expiredAt.isBefore(LocalDateTime.now()))
            throw new IllegalStateException("token expired");

        User user = registrationTokenOptional.get().getUser();
        user.setEnabled(true);

        userRepository.save(user);

        registrationAndChangePasswordTokenRepository.delete(registrationTokenOptional.get());

        SuccessResponse response = new SuccessResponse(
                HttpStatus.OK.value(),
                "Successfully activated user: " + user.getUsername(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<SuccessResponse> forgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty())
            throw new MyEntityNotFoundException("User not found with email: " + email);

        User user = userOptional.get();

        String token = UUID.randomUUID().toString();

        RegistrationAndChangePasswordToken registrationAndChangePasswordToken = new RegistrationAndChangePasswordToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(jwtConfig.getRegistrationTokenExpirationMinutes()),
                user
        );

        registrationAndChangePasswordToken.setChangePassword(true);

        registrationAndChangePasswordTokenRepository.save(registrationAndChangePasswordToken);

        String link = changePasswordLink + token;

        emailSender.send(email, EmailSenderImpl.getChangePasswordHtmlTemplate(user.getUsername(), link), "Change your password");

        SuccessResponse response = new SuccessResponse(
                HttpStatus.OK.value(),
                "Successfully sent link to change password on email: " + email,
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<SuccessResponse> changePassword(ChangePasswordDto changePasswordDto) {
        Optional<RegistrationAndChangePasswordToken> registrationTokenOptional =
                registrationAndChangePasswordTokenRepository.findByTokenAndChangePassword(changePasswordDto.getToken(), true);

        if(registrationTokenOptional.isEmpty())
            throw new MyEntityNotFoundException("Change password token not found");

        LocalDateTime expiredAt = registrationTokenOptional.get().getExpiresAt();

        if(expiredAt.isBefore(LocalDateTime.now()))
            throw new IllegalStateException("token expired");

        if(!changePasswordDto.getPassword().equals(changePasswordDto.getConfirmPassword()))
            throw new IllegalStateException("Password and confirm password doesn't match!");

        User user = registrationTokenOptional.get().getUser();

        user.setPassword(passwordEncoder.encode(changePasswordDto.getPassword()));

        userRepository.save(user);
        
        registrationAndChangePasswordTokenRepository.delete(registrationTokenOptional.get());

        SuccessResponse successResponse = new SuccessResponse(
                HttpStatus.OK.value(),
                "Password was successfully changed for user " + user.getUsername(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Override
    @Transactional
    public SubscribedDto isSubscribed(Long followedId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean isSubscribed = userRepository.isSubscribed(username, followedId);

        return new SubscribedDto(isSubscribed);
    }

    @Override
    @Transactional
    public ResponseEntity<SuccessResponse> updateUser(UserDto userDto) {
        if(!userDto.getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
            throw new MyForbiddenException("You don't have permissions to edit another user!");

        User user = this.userRepository.findById(userDto.getId()).get();
        if(user == null)
            throw new MyEntityNotFoundException("User don't found with id: " + userDto.getId());

        user.getUserInfo().setFirstName(userDto.getFirstName());
        user.getUserInfo().setLastName(userDto.getLastName());
        user.getUserInfo().setStatus(userDto.getStatus());

        userRepository.save(user);

        SuccessResponse successResponse = new SuccessResponse(
                HttpStatus.OK.value(),
                "User " + userDto.getUsername() + " successfully edited",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<SuccessResponse> followUser(Long userId) {
        User currentUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();

        Optional<User> followedUserOptional = userRepository.findById(userId);
        if(followedUserOptional.isEmpty())
            throw new MyEntityNotFoundException("User not found with id: " + userId);

        User followedUser = followedUserOptional.get();

        String message;

        if(currentUser.getFollows().contains(followedUser)){
            currentUser.removeFollow(followedUser);
            message = "unfollowed";
        } else {
            currentUser.addFollow(followedUser);
            message = "followed";
        }

        userRepository.save(currentUser);
        userRepository.save(followedUser);

        if(message.equals("followed"))
            notificationService.createSubscribeNotification(currentUser, followedUser);

        SuccessResponse successResponse = new SuccessResponse(
                HttpStatus.OK.value(),
                "Successfully " + message,
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

}
