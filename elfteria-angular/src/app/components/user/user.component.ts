import {AfterContentInit, AfterViewInit, Component, OnInit} from '@angular/core';
import {UserService} from "../../services/user.service";
import {User} from "../../models/User";
import {AuthService} from "../../services/auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {MatDialog, MatDialogConfig, MatDialogRef} from "@angular/material/dialog";
import {EditProfileComponent} from "../edit-profile/edit-profile.component";
import {Image} from "../../models/Image";
import {ImageService} from "../../services/image.service";
import {CreatePostComponent} from "../create-post/create-post.component";
import {Post} from "../../models/Post";
import {PostService} from "../../services/post.service";
import {formatDate} from "@angular/common";
import {UsersLikedComponent} from "../users-liked/users-liked.component";
import {CommentService} from "../../services/comment.service";
import {NgForm} from "@angular/forms";
import {Comment} from "../../models/Comment";
import {ShowUsersComponent} from "../show-users/show-users.component";
import {ConfirmationDialogComponent} from "../confirmation-dialog/confirmation-dialog.component";
import {StaticMethods} from "../../models/StaticMethods";
import {NgxSpinnerService} from "ngx-spinner";
import {templateJitUrl} from "@angular/compiler";

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
              private router: Router,
              private imageService: ImageService,
              private postService: PostService,
              private commentService: CommentService) { }

  //@ts-ignore
  imagesUrl: string = window["cfgApiBaseUrl"] + "/api/images/";

  user: User = new User();
  currentUser!: User;

  posts: Post[] = [];

  //check if user that watch another user's page is subscribed
  subscribed: boolean = false;
  followWriting: string = "Follow"

  pageNumber: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;

  confirmDialogRef!: MatDialogRef<ConfirmationDialogComponent>;

  isContentInit: boolean = false;

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe(
      data => {
        this.currentUser = data
      }
    )

    // add post to posts array when user create new post
    this.postService.lastPost.subscribe(data => {
      if(data.id){
        setTimeout(() => {
          data.createdDate = StaticMethods.formatToDate(data.createdDate)

          if(data.images.length > 0)
            data.images = data.images.map(img => `${this.imagesUrl}${img}`)

          this.posts.unshift(data)
        }, 1000)

      }
    })
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

  likePost(postId: number, idx: number){
    if(this.posts[idx].liked)
      this.posts[idx].amountOfLikes--;
    else
      this.posts[idx].amountOfLikes++;

    this.posts[idx].liked = !this.posts[idx].liked;

    this.postService.likePost(postId)
  }

  usersThatLiked(postId: number){
    const dialogConfig = new MatDialogConfig();
    dialogConfig.autoFocus = true;
    dialogConfig.width = "300px"

    this.postService.getLikesByPostId(postId).subscribe(data => {
      dialogConfig.data = {usernames: data}
      this.dialog.open(UsersLikedComponent, dialogConfig)
    })
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

        this.getPostsByUserId(+data.id)
      },
      err => {
        alert("Can't find this user")
        this.router.navigateByUrl("/event")
      }
    )
  }

  getPostsByUserId(id: number){
    this.postService.getPostsByUserIdPaginate(id, this.pageNumber, this.pageSize).subscribe(
      data => {
        let tempPosts = data.content
        this.totalPages = data.totalPages

        for(let post of tempPosts){

          post.createdDate = StaticMethods.formatToDate(post.createdDate)

          if(post.images.length > 0){
            post.images = post.images.map(img => `${this.imagesUrl}${img}`)
          }

          if(post.poll){
            post.poll.poll_total_votes = 0;

            post.poll.options.forEach(opt => {
              post.poll.poll_total_votes += opt.votes.length
              if(opt.votes.indexOf(this.currentUser.username) > -1)
                post.poll.voted = true;

            })
          }

          this.commentService.getCommentsByPostId(post.id).subscribe(
            data => {
              post.comments = data

              for(let comm of post.comments)
                comm.createdDate = StaticMethods.formatToDate(comm.createdDate)
            },
            err => {console.log(err)}
          )

          this.posts.push(post)
        }

      },
      error => console.log(error)
    )
  }

  onScroll(){
    if(this.pageNumber >= this.totalPages - 1){
      //console.log("All elements was uploaded!")
    } else {
      this.pageNumber++;
      this.getPostsByUserId(+this.user.id);
    }
  }

  loadComments(idx: number){
    this.posts[idx].showComments = !this.posts[idx].showComments;
  }

  checkCurrent(): boolean{
    if(this.user.username === this.currentUser.username)
      return  true;
    else
      return  false;
  }

  checkAdmin(): boolean{
    if(this.currentUser.roles[0].name === 'ADMIN')
      return true;
    else
      return false;
  }

  deletePost(id: number, idx: number){

    this.confirmDialogRef = this.dialog.open(ConfirmationDialogComponent, {
      disableClose: false
    });

    this.confirmDialogRef.componentInstance.message = "Are you sure you want to delete this post?"

    this.confirmDialogRef.afterClosed().subscribe(result => {
      if(result){
        this.postService.deletePost(id)
        this.posts.splice(idx, 1)
      }
      this.confirmDialogRef.close();
      // @ts-ignore
      this.confirmDialogRef = null;
    })
  }

  sendComment(f: NgForm, postId: number, idx: number) {
    let text = f.controls['commText'].value

    let comment = new Comment();
    comment.username = this.currentUser.username
    comment.text = text;

    this.commentService.createComment(postId, text).subscribe(
      response => {
      },
      err => {
        this.posts[idx].comments.splice(0, 1)
        // console.log(err)
      }
    )
    //@ts-ignore
    comment.createdDate = Date.now()
    this.posts[idx].comments.unshift(comment)

    f.reset()
  }

  usersVoted(optionNumber: number, usernames: string[]){
    const dialogConfig = new MatDialogConfig();
    dialogConfig.autoFocus = true;
    dialogConfig.width = "300px"
    dialogConfig.data = {usernames: usernames}
    this.dialog.open(UsersLikedComponent, dialogConfig)
  }

  voteInPoll(pollId: number, optionNumber: number, postIdx: number){
    this.postService.voteInPoll(pollId, optionNumber).subscribe(response => {
    }, error => {
      this.posts[postIdx].poll.voted = false;
      this.posts[postIdx].poll.poll_total_votes--

      this.posts[postIdx].poll.options.forEach(opt => {
        if(opt.number == optionNumber){
          let idx: number = opt.votes.indexOf(this.currentUser.username)

          opt.votes.splice(idx, 1)
        }

      })
    })

    this.posts[postIdx].poll.options.forEach(opt => {
      if(opt.number == optionNumber)
        opt.votes.push(this.currentUser.username)
    })
    this.posts[postIdx].poll.voted = true;
    this.posts[postIdx].poll.poll_total_votes++
  }
}
