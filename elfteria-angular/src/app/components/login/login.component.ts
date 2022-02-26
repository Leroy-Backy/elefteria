import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {ConfirmationDialogComponent} from "../confirmation-dialog/confirmation-dialog.component";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginFormGroup!: FormGroup;

  confirmDialogRef!: MatDialogRef<ConfirmationDialogComponent>;

  constructor(private formBuilder: FormBuilder,
              private authService: AuthService,
              private router: Router,
              private dialog: MatDialog) { }

  ngOnInit(): void {
    this.loginFormGroup = this.formBuilder.group({
      username: [""],
      password: [""]
    })
  }

  onSubmit() {
    let username = this.loginFormGroup.get("username")?.value;
    let password = this.loginFormGroup.get("password")?.value;

    this.authService.loginUser(username, password).subscribe(
      resp => {
        // @ts-ignore
        localStorage.setItem("jwtToken", resp.headers.get("Authorization"))

        // @ts-ignore
        this.router.navigateByUrl("/user")
      },
      err => {
        //alert("Failed to login: " + err)
        this.failedLoginDialog();
      }
    )
  }

  failedLoginDialog(){
    this.confirmDialogRef = this.dialog.open(ConfirmationDialogComponent)

    this.confirmDialogRef.componentInstance.message = "Failed to login, try again";

    this.confirmDialogRef.afterClosed().subscribe(res => {
      this.confirmDialogRef.close();
      //@ts-ignore
      this.confirmDialogRef = null;
    })
  }
}
