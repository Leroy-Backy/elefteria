package org.elefteria.elefteriasn.controller;

import org.elefteria.elefteriasn.dto.NotificationDto;
import org.elefteria.elefteriasn.response.SuccessResponse;
import org.elefteria.elefteriasn.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @GetMapping("")
    public List<NotificationDto> getAllNotificationsForCurrentUser(){
        return notificationService.getNotificationsByOwnerUsername();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @GetMapping("/unreadAmount")
    public Integer getCountOfUnreadNotifications(){
        return notificationService.getCountOfUnreadNotifications();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_CITIZEN')")
    @PostMapping("/read")
    public ResponseEntity<SuccessResponse> readNotifications(){
        return notificationService.makeNotificationsRead();
    }
}
