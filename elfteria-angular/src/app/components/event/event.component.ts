import {AfterContentInit, Component, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {PostService} from "../../services/post.service";
import {CommentService} from "../../services/comment.service";
import {ImageService} from "../../services/image.service";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {Post} from "../../models/Post";
import {NgForm} from "@angular/forms";
import {Comment} from "../../models/Comment";
import {formatDate} from "@angular/common";
import {UsersLikedComponent} from "../users-liked/users-liked.component";
import {User} from "../../models/User";
import {StaticMethods} from "../../models/StaticMethods";
import {PostMode} from "../posts/PostMode";

@Component({
  selector: 'app-event',
  templateUrl: './event.component.html',
  styleUrls: ['./event.component.css']
})
export class EventComponent implements OnInit {

  constructor() { }

  mode: PostMode = PostMode.POPULAR;

  ngOnInit(): void {
  }

}
