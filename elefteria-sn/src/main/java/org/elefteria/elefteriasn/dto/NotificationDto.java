package org.elefteria.elefteriasn.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.elefteria.elefteriasn.entity.Notification;
import org.elefteria.elefteriasn.entity.NotificationType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NotificationDto {
    private Long id;
    private String actorUsername;
    private NotificationType type;
    private Long postId;
    private String commentText;
    private String actorImage;
    private LocalDateTime createdDate;
    private LocalDateTime postCreatedDate;
    private boolean read;

    public NotificationDto(Notification notification){
        this.id = notification.getId();
        this.actorUsername = notification.getActor().getUsername();
        this.type = notification.getType();
        this.createdDate = notification.getCreatedDate();
        this.read = notification.isRead();

        if(notification.getPost() != null) {
            this.postId = notification.getPost().getId();
            this.postCreatedDate = notification.getPost().getCreatedDate();
        }
        if(notification.getComment() != null)
            this.commentText = notification.getComment().getText();

        if(notification.getActor().getUserInfo().getAvatar() != null)
            this.actorImage = notification.getActor().getUserInfo().getAvatar().getFileName();
    }

    public static NotificationDto formatToNotificationDto(Notification notification){
        return new NotificationDto(notification);
    }
}
