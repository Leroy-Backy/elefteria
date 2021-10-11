import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {EventComponent} from "./components/event/event.component";
import {UserComponent} from "./components/user/user.component";
import {LoginComponent} from "./components/login/login.component";
import {RegisterComponent} from "./components/register/register.component";
import {AuthGuard} from "./auth.guard";
import {FeedComponent} from "./components/feed/feed.component";
import {SearchUsersComponent} from "./components/search-users/search-users.component";
import {RegistrationConfimComponent} from "./components/registration-confim/registration-confim.component";
import {ChatComponent} from "./components/chat/chat.component";
import {ChangePasswordComponent} from "./components/change-password/change-password.component";
import {ChangePasswordConfirmComponent} from "./components/change-password-confirm/change-password-confirm.component";
import {NotificationComponent} from "./components/notification/notification.component";
import {ShowPostComponent} from "./components/show-post/show-post.component";

const routes: Routes = [
  {path: 'notifications', component: NotificationComponent, canActivate: [AuthGuard]},
  {path: 'changePassword', component: ChangePasswordComponent},
  {path: 'changePasswordConfirm/:token', component: ChangePasswordConfirmComponent},
  {path: 'register/confirm/:token', component: RegistrationConfimComponent},
  {path: 'search/:keyword', component: SearchUsersComponent, canActivate: [AuthGuard]},
  {path: 'chat', component: ChatComponent, canActivate: [AuthGuard]},
  {path: 'search', component: SearchUsersComponent, canActivate: [AuthGuard]},
  {path: 'feed', component: FeedComponent, canActivate: [AuthGuard]},
  {path: 'popular', component: EventComponent, canActivate: [AuthGuard]},
  {path: 'user/:username', component: UserComponent, canActivate: [AuthGuard]},
  {path: 'user', component: UserComponent, canActivate: [AuthGuard]},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: '', redirectTo: 'popular', pathMatch: 'full'},
  {path: '**', redirectTo: 'popular', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
