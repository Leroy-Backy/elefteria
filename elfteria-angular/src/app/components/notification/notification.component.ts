import {Component, OnInit} from '@angular/core';
import {NotificationService} from "../../services/notification.service";
import {Notification} from "../../models/Notification";
import {NotificationType} from "./NotificationType";
import {StaticMethods} from "../../models/StaticMethods";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {ShowPostComponent} from "../show-post/show-post.component";
import {Router} from "@angular/router";

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.css']
})
export class NotificationComponent implements OnInit {

  constructor(private notificationService: NotificationService,
              private dialog: MatDialog,
              private router: Router) { }

  notifications: Notification[] = [];

  //@ts-ignore
  imagesUrl: string = window["cfgApiBaseUrl"] + "/api/images/";

  like: NotificationType = NotificationType.LIKE;
  comment: NotificationType = NotificationType.COMMENT;
  subscription: NotificationType = NotificationType.SUBSCRIPTION;

  ngOnInit(): void {
    this.notificationService.getNotifications().subscribe(data => {
      for(let notification of data){
        notification.createdDate = StaticMethods.formatToDate(notification.createdDate)

        if(notification.actorImage)
          notification.actorImage = this.imagesUrl + notification.actorImage

        if(notification.postCreatedDate)
          notification.postCreatedDate = StaticMethods.formatToDate(notification.postCreatedDate)

        this.notifications.unshift(notification)
      }

      this.notificationService.setNotificationsRead().subscribe(res => {}, err => {console.log(err)})
    }, err => {console.log(err)})
  }

  openPost(postId: number){
    let dialogConfig = new MatDialogConfig();
    dialogConfig.autoFocus = true;
    dialogConfig.data = {postId: postId}
    let dialogRef = this.dialog.open(ShowPostComponent, dialogConfig)
    this.router.events.subscribe(() => {dialogRef.close()})
  }
}
