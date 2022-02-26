import {NotificationType} from "../components/notification/NotificationType";

export class Notification{
  id!: number;
  actorUsername!: number;
  type!: NotificationType;
  postId!: number;
  commentText!: string;
  actorImage!: string;
  createdDate!: Date;
  postCreatedDate!: Date;
  read!: boolean;
}
