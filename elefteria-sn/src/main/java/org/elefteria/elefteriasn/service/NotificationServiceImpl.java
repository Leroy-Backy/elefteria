package org.elefteria.elefteriasn.service;

import org.elefteria.elefteriasn.dao.NotificationRepository;
import org.elefteria.elefteriasn.dao.UserRepository;
import org.elefteria.elefteriasn.dto.NotificationDto;
import org.elefteria.elefteriasn.entity.*;
import org.elefteria.elefteriasn.exception.MyEntityNotFoundException;
import org.elefteria.elefteriasn.response.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService{

    private NotificationRepository notificationRepository;
    private UserRepository userRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<SuccessResponse> makeNotificationsRead() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        notificationRepository.makeNotificationsRead(username);

        SuccessResponse successResponse = new SuccessResponse(
                HttpStatus.OK.value(),
                "notifications read",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Override
    @Transactional
    public Integer getCountOfUnreadNotifications() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Integer count = notificationRepository.countByOwnerUsernameAndRead(username, false);

        return count;
    }

    @Override
    @Transactional
    public List<NotificationDto> getNotificationsByOwnerUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        List<Notification> notifications = notificationRepository.findByOwnerUsernameOrderByCreatedDateAsc(username);

        List<NotificationDto> notificationDtos = notifications.stream().map(NotificationDto::formatToNotificationDto).collect(Collectors.toList());

        notificationRepository.deleteAllExceptFirstTenUnread(username);

        return notificationDtos;
    }

    @Override
    @Transactional
    public void createSubscribeNotification(User userActor, User userOwner) {
        Notification notification = createNotification(userOwner, userActor, NotificationType.SUBSCRIPTION);

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void createCommentNotification(User userActor, Post post, Comment comment) {
        User userOwner = post.getUser();
        if(userOwner.getUsername().equals(userActor.getUsername()))
            return;

        Notification notification = createNotification(userOwner, userActor, NotificationType.COMMENT);
        notification.setPost(post);
        notification.setComment(comment);

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void createLikeNotification(String actorUsername, Post post) {
        User userOwner = post.getUser();
        if(userOwner.getUsername().equals(actorUsername))
            return;

        Optional<User> userOptional = userRepository.findByUsername(actorUsername);
        if(userOptional.isEmpty())
            throw new MyEntityNotFoundException("Can't find user " + actorUsername);

        Notification notification = createNotification(userOwner, userOptional.get(), NotificationType.LIKE);
        notification.setPost(post);

        notificationRepository.save(notification);
    }

    private Notification createNotification(User userOwner, User userActor, NotificationType type){
        Notification notification = new Notification();
        notification.setOwner(userOwner);
        notification.setActor(userActor);
        notification.setType(type);
        return notification;
    }
}
