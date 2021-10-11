import {AfterContentInit, AfterViewInit, Component, OnInit} from '@angular/core';
import {UserService} from "../../services/user.service";
import {User} from "../../models/User";
import {AuthService} from "../../services/auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {EditProfileComponent} from "../edit-profile/edit-profile.component";
import {CreatePostComponent} from "../create-post/create-post.component";
import {ShowUsersComponent} from "../show-users/show-users.component";
import {PostMode} from "../posts/PostMode";
import {BehaviorSubject} from "rxjs";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit, AfterContentInit, AfterViewInit {

  constructor(private userService: UserService,
              private authService: AuthService,
              private route: ActivatedRoute,
              private dialog: MatDialog,
              private router: Router) { }

  //@ts-ignore
  imagesUrl: string = window["cfgApiBaseUrl"] + "/api/images/";

  mode: PostMode = PostMode.USER;

  user: User = new User();
  currentUser!: User;

  //check if user that watch another user's page is subscribed
  subscribed: boolean = false;
  followWriting: string = "Follow"

  isContentInit: boolean = false;
  // call when have to load new user's posts
  userId: BehaviorSubject<number> = new BehaviorSubject<number>(0);

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe(
      data => {
        this.currentUser = data
      }
    )
  }

  ngAfterContentInit(): void {
    this.isContentInit = true;
  }

  ngAfterViewInit() {
    this.route.paramMap.subscribe(() =>
      {this.userHandler()}
    )
  }

  userHandler(){
    let usernameParam: boolean = this.route.snapshot.paramMap.has("username");

    if(usernameParam){
      // @ts-ignore
      this.getUserByUsername(this.route.snapshot.paramMap.get("username"));

    } else {
      this.getUserByUsername(this.currentUser.username)
    }
  }

  // if mode is 1, then show followers, else show follows
  showFollowersOrFollows(mode: number, id: number){
    const dialogConfig = new MatDialogConfig();
    dialogConfig.autoFocus = true;
    dialogConfig.data = {mode: mode, id: id}
    this.dialog.open(ShowUsersComponent, dialogConfig)
  }

  editProfile(){
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    this.dialog.open(EditProfileComponent, dialogConfig)
  }

  createPost() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    this.dialog.open(CreatePostComponent, dialogConfig)
  }

  followUser(id: number){
    this.subscribed = !this.subscribed

    if(this.subscribed) {
      this.followWriting = "Unfollow"
      this.user.amountOfFollowers = (+this.user.amountOfFollowers + 1).toString()
    } else {
      this.followWriting = "Follow"
      this.user.amountOfFollowers = (+this.user.amountOfFollowers - 1).toString()
    }

    this.userService.followUser(id)
  }

  getUserByUsername(username: string){
    this.userService.getUserByUsername(username).subscribe(
      data => {
        this.user = data

        if(data.avatar){
          this.user.avatar = this.imagesUrl + data.avatar;
        }

        this.userService.isSubscribed(+data.id).subscribe(data => {
          this.subscribed = data
          if(data)
            this.followWriting = "Unfollow"
          else
            this.followWriting = "Follow"
        })

        this.userId.next(+data.id)
      },
      err => {
        alert("Can't find this user")
        this.router.navigateByUrl("/event")
      }
    )
  }

  checkCurrent(): boolean{
    if(this.user.username === this.currentUser.username)
      return  true;
    else
      return  false;
  }
}
