import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {User} from "../models/User";
import {map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  // @ts-ignore
  private usersUrl: string = window["cfgApiBaseUrl"] + "/api/users"

  constructor(private httpClient: HttpClient) { }

  isSubscribed(id: number): Observable<boolean>{
    return this.httpClient.get<SubscribedResponse>(`${this.usersUrl}/isSubscribed/${id}`).pipe(
      map(response => response.subscribed)
    )
  }

  activateAccount(token: string){
    let url = `${this.usersUrl}/activateUser?token=${token}`;

    return this.httpClient.get<any>(url)
  }

  searchUsers(keyword: string, page: number, size: number): Observable<UsersPaginateResponse>{
    const url = `${this.usersUrl}/search/?keyword=${keyword}&page=${page}&size=${size}`

    return this.httpClient.get<UsersPaginateResponse>(url);
  }

  getAllUsers(page: number, size: number): Observable<UsersPaginateResponse>{
    const url = `${this.usersUrl}?page=${page}&size=${size}`

    return this.httpClient.get<UsersPaginateResponse>(url);
  }

  getFollowersByIdPaginate(id: number, page: number, size: number): Observable<UsersPaginateResponse>{
    const url = `${this.usersUrl}/${id}/followers?page=${page}&size=${size}`;

    return this.httpClient.get<UsersPaginateResponse>(url);
  }

  getFollowsByIdPaginate(id: number, page: number, size: number): Observable<UsersPaginateResponse>{
    const url = `${this.usersUrl}/${id}/follows?page=${page}&size=${size}`;

    return this.httpClient.get<UsersPaginateResponse>(url);
  }

  followUser(id: number){
    return this.httpClient.post(`${this.usersUrl}/${id}/follow`, {}, {observe: "response"}).subscribe(
      response => console.log(response.body),
      err => console.log(err.message)
    )
  }

  editUser(user: User){
    return this.httpClient.put<any>(this.usersUrl, user, {observe: "response"})
  }

  getUserByUsername(username: string): Observable<User>{
    return this.httpClient.get<User>(this.usersUrl + "/" + username);
  }
}

interface UsersPaginateResponse{
  content:  User[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

interface SubscribedResponse{
  subscribed: boolean;
}
