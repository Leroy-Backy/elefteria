import {Comment} from "./Comment";
import {Poll} from "./Poll";

export class Post{
  id!: number;
  title!: string;
  amountOfLikes!: number;
  text!: string;
  createdDate!: Date;
  username!: string;
  images!: String[];
  liked: boolean = false;
  showComments: boolean = false;
  comments!: Comment[];
  poll!: Poll;
}
