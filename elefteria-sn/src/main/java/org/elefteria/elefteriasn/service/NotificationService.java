package org.elefteria.elefteriasn.service;

import org.elefteria.elefteriasn.dto.NotificationDto;
import org.elefteria.elefteriasn.entity.Comment;
import org.elefteria.elefteriasn.entity.Post;
import org.elefteria.elefteriasn.entity.User;
import org.elefteria.elefteriasn.response.SuccessResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface NotificationService {
    void createLikeNotification(String actorUsername, Post post);

    void createCommentNotification(User userActor, Post post, Comment comment);

    void createSubscribeNotification(User userActor, User userOwner);

    List<NotificationDto> getNotificationsByOwnerUsername();

    Integer getCountOfUnreadNotifications();

    ResponseEntity<SuccessResponse> makeNotificationsRead();
}
