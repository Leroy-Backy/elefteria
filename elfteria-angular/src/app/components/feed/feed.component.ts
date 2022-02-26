import {Component, OnInit} from '@angular/core';
import {PostMode} from "../posts/PostMode";

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.css']
})
export class FeedComponent implements OnInit{

  constructor() { }

  mode: PostMode = PostMode.FEED;

  ngOnInit(): void {
  }

}
