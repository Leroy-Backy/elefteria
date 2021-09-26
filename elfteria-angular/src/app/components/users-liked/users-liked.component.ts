import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Router} from "@angular/router";

@Component({
  selector: 'app-users-liked',
  templateUrl: './users-liked.component.html',
  styleUrls: ['./users-liked.component.css']
})
export class UsersLikedComponent implements OnInit {

  constructor(private dialogRef: MatDialogRef<UsersLikedComponent>,
              @Inject(MAT_DIALOG_DATA) public dialogData: any,
              private router: Router) { }

  usernames: string[] = [];

  ngOnInit(): void {
    this.usernames = this.dialogData.usernames;
  }

  goToUser(username: string) {
    this.router.navigate(["/user/" + username]).then(
      () => {location.reload()})
    this.dialogRef.close();
  }
}
