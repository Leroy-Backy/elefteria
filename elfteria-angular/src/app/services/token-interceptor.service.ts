import { Injectable } from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {AuthService} from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class TokenInterceptorService implements HttpInterceptor{

  constructor(private authService: AuthService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if(this.authService.loggedIn()){
      // @ts-ignore
      let tokenizedReq = req.clone({headers: req.headers.set("Authorization", localStorage.getItem("jwtToken"))})
      return next.handle(tokenizedReq)
    } else {
      return next.handle(req)
    }

  }
}
