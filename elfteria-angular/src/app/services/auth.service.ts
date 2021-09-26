import { Injectable } from '@angular/core';
import {RegisterDto} from "../models/RegisterDto";
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {User} from "../models/User";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  // @ts-ignore
  private userUrl: string = window["cfgApiBaseUrl"] +  "/api/users"

  // @ts-ignore
  private loginUrl: string = window["cfgApiBaseUrl"] + "/api/login"

  constructor(private httpClient: HttpClient,
              private router: Router) { }

  forgotPassword(email: string){
    const url = `${this.userUrl}/changePassword?email=${email}`;

    return this.httpClient.get<any>(url);
  }

  changePassword(password: string, token: string){
    const url = `${this.userUrl}/changePassword`;

    return this.httpClient.post<any>(url, {password: password, confirmPassword: password, token: token});
  }

  registerUser(user: RegisterDto){
    return this.httpClient.post<any>(this.userUrl, user)
  }

  loginUser(username: string, password: string){
    return this.httpClient.post<any>(this.loginUrl, {username: username, password: password},
      {observe: "response"})
  }

  loggedIn(){
    return !!localStorage.getItem("jwtToken")
  }

  logout(){
    localStorage.removeItem("jwtToken")
    this.router.navigateByUrl("/login")
  }

  getCurrentUser(): Observable<User>{
    return this.httpClient.get<User>(`${this.userUrl}/current/get`);
  }
}
