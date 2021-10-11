import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {NotificationService} from "../../services/notification.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  constructor(private authService: AuthService,
              private notificationService: NotificationService) { }

  unreadNotifications: number = 0;

  ngOnInit(): void {
    this.notificationService.getAmountOfUnreadNotifications().subscribe(data => {
      this.unreadNotifications = data;
    }, err => {console.log(err)})
  }

  loggedIn(): boolean{
    return this.authService.loggedIn();
  }

  logOut(){
    this.authService.logout();
  }
}
