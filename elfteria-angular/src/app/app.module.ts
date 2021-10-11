import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { UserComponent } from './components/user/user.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import { RegisterComponent } from './components/register/register.component';
import { LoginComponent } from './components/login/login.component';
import { HeaderComponent } from './components/header/header.component';
import { EventComponent } from './components/event/event.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {AuthService} from "./services/auth.service";
import {AuthGuard} from "./auth.guard";
import {TokenInterceptorService} from "./services/token-interceptor.service";
import {ErrorInterceptorService} from "./services/error-interceptor.service";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MaterialModule} from "./material/material.module";
import { EditProfileComponent } from './components/edit-profile/edit-profile.component';
import { CreatePostComponent } from './components/create-post/create-post.component';
import { UsersLikedComponent } from './components/users-liked/users-liked.component';
import {MatFormFieldModule} from "@angular/material/form-field";
import { ShowUsersComponent } from './components/show-users/show-users.component';
import { FeedComponent } from './components/feed/feed.component';
import {InfiniteScrollModule} from "ngx-infinite-scroll";
import { SearchUsersComponent } from './components/search-users/search-users.component';
import { ConfirmationDialogComponent } from './components/confirmation-dialog/confirmation-dialog.component';
import { RegistrationConfimComponent } from './components/registration-confim/registration-confim.component';
import { ChatComponent } from './components/chat/chat.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { ChangePasswordConfirmComponent } from './components/change-password-confirm/change-password-confirm.component';
import {ImageCropperModule} from "ngx-image-cropper";
import {NgxSpinnerModule} from "ngx-spinner";
import { PostsComponent } from './components/posts/posts.component';
import { NotificationComponent } from './components/notification/notification.component';
import { ShowPostComponent } from './components/show-post/show-post.component';

@NgModule({
  declarations: [
    AppComponent,
    UserComponent,
    RegisterComponent,
    LoginComponent,
    HeaderComponent,
    EventComponent,
    EditProfileComponent,
    CreatePostComponent,
    UsersLikedComponent,
    ShowUsersComponent,
    FeedComponent,
    SearchUsersComponent,
    ConfirmationDialogComponent,
    RegistrationConfimComponent,
    ChatComponent,
    ChangePasswordComponent,
    ChangePasswordConfirmComponent,
    PostsComponent,
    NotificationComponent,
    ShowPostComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        MaterialModule,
        MatFormFieldModule,
        FormsModule,
        InfiniteScrollModule,
        ImageCropperModule,
        NgxSpinnerModule
    ],
  providers: [AuthService, AuthGuard,
    {provide: HTTP_INTERCEPTORS, useClass: TokenInterceptorService, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptorService, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule { }
