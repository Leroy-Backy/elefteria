import {AfterContentInit, Component, OnInit} from '@angular/core';
import {PostService} from "../../services/post.service";
import {Post} from "../../models/Post";
import {formatDate} from "@angular/common";
import {CommentService} from "../../services/comment.service";
import {ImageService} from "../../services/image.service";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {UsersLikedComponent} from "../users-liked/users-liked.component";
import {NgForm} from "@angular/forms";
import {Comment} from "../../models/Comment";
import {User} from "../../models/User";
import {AuthService} from "../../services/auth.service";
import {StaticMethods} from "../../models/StaticMethods";

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.css']
})
export class FeedComponent implements OnInit, AfterContentInit {

  constructor(private postService: PostService,
              private commentService: CommentService,
              private imageService: ImageService,
              private authService: AuthService,
              private dialog: MatDialog) { }

  //@ts-ignore
  imagesUrl: string = window["cfgApiBaseUrl"] + "/api/images/";

  posts: Post[] = [];

  currentUser!: User;

  pageNumber: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe(
      data => {this.currentUser = data}
    )
  }

  ngAfterContentInit() {
    this.addPostsFeed();
  }

  addPostsFeed(){
    this.postService.getPostsFeedPaginate(this.pageNumber, this.pageSize).subscribe(
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

          this.posts.push(post);
        }

      },
      error => console.log(error)
    )
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
        if(response.status == 200){
          // @ts-ignore
          comment.createdDate = Date.now()
          this.posts[idx].comments.push(comment)
        }
      },
      err => {
        console.log(err.message)
        alert(err.message)
      }
    )

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
    if(this.pageNumber >= this.totalPages - 1){
      //console.log("All elements was uploaded!")
    } else {
      this.pageNumber++;
      this.addPostsFeed();
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
}
