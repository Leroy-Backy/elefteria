import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {PostMode} from "../posts/PostMode";
import {Router} from "@angular/router";

@Component({
  selector: 'app-show-post',
  templateUrl: './show-post.component.html',
  styleUrls: ['./show-post.component.css']
})
export class ShowPostComponent implements OnInit {

  constructor(private dialogRef: MatDialogRef<ShowPostComponent>,
              @Inject(MAT_DIALOG_DATA) private dialogData: any) { }

  postId!: number;
  mode: PostMode = PostMode.SINGLE;

  ngOnInit(): void {
    this.postId = this.dialogData.postId;
  }

  onClose() {
    this.dialogRef.close()
  }
}
