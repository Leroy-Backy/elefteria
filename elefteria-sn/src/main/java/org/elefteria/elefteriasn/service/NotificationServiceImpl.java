package org.elefteria.elefteriasn.service;

//import org.elefteria.elefteriasn.dao.NotificationRepository;
//import org.elefteria.elefteriasn.entity.Notification;
import org.elefteria.elefteriasn.entity.NotificationType;
import org.elefteria.elefteriasn.entity.Post;
import org.elefteria.elefteriasn.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//@Service
//public class NotificationServiceImpl implements NotificationService{
//
//    private NotificationRepository notificationRepository;
//
//    @Autowired
//    public NotificationServiceImpl(NotificationRepository notificationRepository) {
//        this.notificationRepository = notificationRepository;
//    }
//
//    @Override
//    @Transactional
//    public void createLikeNotification(User owner, User actor, Post post) {
//        Notification notification = new Notification();
//        notification.setOwner(owner);
//        notification.setActor(actor);
//        notification.setPost(post);
//        notification.setType(NotificationType.LIKE);
//
//        notificationRepository.save(notification);
//    }
//}
