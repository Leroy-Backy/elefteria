import {AfterContentInit, Component, Input, OnInit} from '@angular/core';
import {PostMode} from "./PostMode";
import {Post} from "../../models/Post";
import {User} from "../../models/User";
import {AuthService} from "../../services/auth.service";
import {PostService} from "../../services/post.service";
import {CommentService} from "../../services/comment.service";
import {MatDialog, MatDialogConfig, MatDialogRef} from "@angular/material/dialog";
import {StaticMethods} from "../../models/StaticMethods";
import {NgForm} from "@angular/forms";
import {Comment} from "../../models/Comment";
import {UsersLikedComponent} from "../users-liked/users-liked.component";
import {ConfirmationDialogComponent} from "../confirmation-dialog/confirmation-dialog.component";
import {BehaviorSubject, Subject} from "rxjs";

@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.css']
})
export class PostsComponent implements OnInit, AfterContentInit{

  @Input() mode!: PostMode;
  userId: number = 0;
  @Input() onChange: BehaviorSubject<number> = new BehaviorSubject<number>(0)
  @Input() postId!: number;

  popularMode: PostMode = PostMode.POPULAR
  singleMode: PostMode = PostMode.SINGLE

  //@ts-ignore
  imagesUrl: string = window["cfgApiBaseUrl"] + "/api/images/";

  posts: Post[] = [];

  currentUser!: User;

  currentUserLoaded: Subject<boolean> = new Subject<boolean>()

  isContentInit: boolean = false;

  pageNumber: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;

  confirmDialogRef!: MatDialogRef<ConfirmationDialogComponent>;

  constructor(private postService: PostService,
              private commentService: CommentService,
              private authService: AuthService,
              private dialog: MatDialog) { }

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe(
      data => {
        this.currentUser = data
        this.currentUserLoaded.next(true)
      }
    )

    if(this.mode == PostMode.USER){
      // add post to posts array when user create new post
      this.postService.lastPost.subscribe(data => {
        setTimeout(() => {
          data.createdDate = StaticMethods.formatToDate(data.createdDate)

          if(data.images.length > 0){
            if(!data.images[0].startsWith("http"))
              data.images = data.images.map(img => `${this.imagesUrl}${img}`)
          }

          this.posts.unshift(data)
        }, 1000)
      })

      // subscribe on subject that is triggered when profile page has changed to load posts for new user
      this.currentUserLoaded.subscribe(res => {
        this.onChange.subscribe(data => {
          if(data != 0) {
            this.userId = data
            this.reset()
          }
        })
      })
    }
  }

  ngAfterContentInit() {
    if(this.mode == PostMode.FEED)
      this.addPostsFeed()
    else if(this.mode == PostMode.POPULAR)
      this.getPostsPopular()
    else if(this.mode == PostMode.SINGLE)
      this.getPostById(this.postId)

    setTimeout(() => {this.isContentInit = true}, 2000)
  }

  reset(){
    this.posts = [];
    this.pageNumber = 0;
    this.totalPages = 0;

    this.getPostsByUserId(this.userId)

    //setTimeout(() => {this.isContentInit = true}, 2000)
  }

  checkAdmin(): boolean{
    if(this.currentUser.roles[0].name === 'ADMIN')
      return true;
    else
      return false;
  }

  checkCurrent(username: string) : boolean {
    if(this.currentUser.username === username)
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

  loadComments(idx: number){
    this.posts[idx].showComments = !this.posts[idx].showComments;
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
        console.log(err.message)
      }
    )
    // @ts-ignore
    comment.createdDate = Date.now()
    this.posts[idx].comments.unshift(comment)

    f.reset()
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

  onScroll() {
    if((this.pageNumber >= this.totalPages - 1) || this.mode == PostMode.SINGLE){
      //console.log("All elements was uploaded!")
    } else {
      this.pageNumber++;

      if(this.mode == PostMode.FEED)
        this.addPostsFeed()
      else if(this.mode == PostMode.POPULAR)
        this.getPostsPopular()
      else if(this.mode == PostMode.USER)
        this.getPostById(this.userId)
    }
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

  getPostById(postId: number){
    this.postService.getPostById(postId).subscribe(data => {
      data.createdDate = StaticMethods.formatToDate(data.createdDate)

      if(data.images.length > 0)
        data.images = data.images.map(img => `${this.imagesUrl}${img}`)

      if(data.poll){
        data.poll.poll_total_votes = 0;

        data.poll.options.forEach(opt => {
          data.poll.poll_total_votes += opt.votes.length
          if(opt.votes.indexOf(this.currentUser.username) > -1)
            data.poll.voted = true;

        })
      }

      this.commentService.getCommentsByPostId(data.id).subscribe(
        res => {
          data.comments = res

          for(let comm of data.comments)
            comm.createdDate = StaticMethods.formatToDate(comm.createdDate)
        },
        err => {console.log(err)}
      )

      this.posts.push(data);
    })
  }

  addPostsFeed(){
    this.postService.getPostsFeedPaginate(this.pageNumber, this.pageSize).subscribe(
      data => {
        let tempPosts = data.content
        this.totalPages = data.totalPages
        for(let post of tempPosts){

          post.createdDate = StaticMethods.formatToDate(post.createdDate)

          if(post.images.length > 0)
            post.images = post.images.map(img => `${this.imagesUrl}${img}`)


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

          this.posts.push(post);
        }

      },
      error => console.log(error)
    )
  }

  getPostsByUserId(id: number){
    this.postService.getPostsByUserIdPaginate(id, this.pageNumber, this.pageSize).subscribe(
      data => {
        let tempPosts = data.content
        this.totalPages = data.totalPages

        for(let post of tempPosts){

          post.createdDate = StaticMethods.formatToDate(post.createdDate)

          if(post.images.length > 0)
            post.images = post.images.map(img => `${this.imagesUrl}${img}`)


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

  getPostsPopular(){
    this.postService.getPopularPosts(this.pageNumber, this.pageSize).subscribe(
      data => {
        let tempPosts = data.content;
        this.totalPages = data.totalPages;

        for(let post of tempPosts){

          post.createdDate = StaticMethods.formatToDate(post.createdDate)

          if(post.images.length > 0)
            post.images = post.images.map(img => `${this.imagesUrl}${img}`)


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
      error => {console.log(error)}
    )
  }

}
