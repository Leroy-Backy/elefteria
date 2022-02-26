import {Component, Inject, OnInit} from '@angular/core';
import {UserService} from "../../services/user.service";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {User} from "../../models/User";
import {Router} from "@angular/router";
import {ImageService} from "../../services/image.service";

@Component({
  selector: 'app-show-users',
  templateUrl: './show-users.component.html',
  styleUrls: ['./show-users.component.css']
})
export class ShowUsersComponent implements OnInit {

  constructor(private userService: UserService,
              private dialogRef: MatDialogRef<ShowUsersComponent>,
              private router: Router,
              @Inject(MAT_DIALOG_DATA) public dialogData: any) { }

  //@ts-ignore
  imagesUrl: string = window["cfgApiBaseUrl"] + "/api/images/";

  users: User[] = []
  id!: number;
  mode!: number;

  pageNumber: number = 0;
  pageSize: number = 12;
  totalPages!: number;

  ngOnInit(): void {
    this.mode = this.dialogData.mode
    this.id = this.dialogData.id

    if(this.mode == 1){
      this.getFollowers(this.id)
    } else {
      this.getFollows(this.id)
    }
  }

  getFollowers(id: number){
    this.userService.getFollowersByIdPaginate(id, this.pageNumber, this.pageSize).subscribe(
      data => {
        let tempUsers = data.content
        this.totalPages = data.totalPages

        for(let user of tempUsers){
          if(user.avatar)
            user.avatar = this.imagesUrl + user.avatar;

          this.users.push(user);
        }
      },
      err => console.log(err.message)
    )
  }

  getFollows(id: number){
    this.userService.getFollowsByIdPaginate(id, this.pageNumber, this.pageSize).subscribe(
      data => {
        let tempUsers = data.content
        this.totalPages = data.totalPages

        for(let user of tempUsers){
          if(user.avatar)
            user.avatar = this.imagesUrl + user.avatar;

          this.users.push(user)
        }
      },
      err => console.log(err.message)
    )
  }

  onScroll(){
    if(this.pageNumber >= this.totalPages - 1){
      console.log("All elements was uploaded!")
    } else {
      this.pageNumber++;
      if(this.mode == 1){
        this.getFollowers(this.id)
      } else {
        this.getFollows(this.id)
      }

    }
  }

  goToUser(username: string) {
    this.router.navigate(["/user/" + username]).then(
      () => {location.reload()})
    this.dialogRef.close();
  }

  onClose() {
    this.dialogRef.close()
  }
}
