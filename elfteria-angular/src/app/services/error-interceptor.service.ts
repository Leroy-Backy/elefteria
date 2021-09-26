import { Injectable } from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {AuthService} from "./auth.service";
import {catchError} from "rxjs/operators";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class ErrorInterceptorService implements HttpInterceptor{

  constructor(private authService: AuthService, private router: Router) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(catchError(err => {
      if(err.status == 401 || err.status == 403){
        this.authService.logout();
        this.router.navigateByUrl("/login").then(location.reload)
      }

      const error = err.error.message || err.statusText;

      return throwError(error)
    }))

  }
}
