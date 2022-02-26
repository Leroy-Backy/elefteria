
import {Role} from "./Role";

export class User {
  id!: string;
  email!: string;
  username!: string;
  firstName!: string;
  lastName!: string;
  status!: string;
  avatar!: string;
  amountOfFollowers!: string;
  amountOfFollows!: string;
  roles!: Role[];
}
