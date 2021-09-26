import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {NgForm} from "@angular/forms";
import {ConfirmationDialogComponent} from "../confirmation-dialog/confirmation-dialog.component";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {

  confirmDialogRef!: MatDialogRef<ConfirmationDialogComponent>;

  constructor(private authService: AuthService,
              private dialog: MatDialog) { }

  ngOnInit(): void {
  }

  sendEmail(sendForm: NgForm) {
    let email: string = sendForm.value.email;

    this.authService.forgotPassword(email).subscribe(res => {}, err => {
      //change dialog message to error message if there is error
      this.confirmDialogRef.componentInstance.message = "Failed: " + err
    })

    this.infoWindow()
  }

  // show dialog with info
  infoWindow(){
    this.confirmDialogRef = this.dialog.open(ConfirmationDialogComponent)

    this.confirmDialogRef.componentInstance.message = "We sent you link on email address to change your password"

    this.confirmDialogRef.afterClosed().subscribe(res => {
      this.confirmDialogRef.close();
      // @ts-ignore
      this.confirmDialogRef = null;
    })
  }
}
