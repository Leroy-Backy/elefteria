import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Notification} from "../models/Notification";

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  // @ts-ignore
  private notificationUrl: string = window["cfgApiBaseUrl"] + "/api/notifications"

  constructor(private client: HttpClient) { }

  getNotifications(): Observable<Notification[]>{
    return this.client.get<Notification[]>(this.notificationUrl)
  }

  getAmountOfUnreadNotifications(){
    return this.client.get<number>(this.notificationUrl + "/unreadAmount")
  }

  setNotificationsRead(){
    const url = `${this.notificationUrl}/read`;

    return this.client.post(url, {observe: "response"});
  }
}
