import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {RegisterDto} from "../../models/RegisterDto";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {MyValidators} from "../../validators/MyValidators";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {ConfirmationDialogComponent} from "../confirmation-dialog/confirmation-dialog.component";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  registerFormGroup!: FormGroup;

  confirmDialogRef!: MatDialogRef<ConfirmationDialogComponent>;

  constructor(private formBuilder: FormBuilder,
              private authService: AuthService,
              private router: Router,
              private dialog: MatDialog) { }

  ngOnInit(): void {
    this.registerFormGroup = this.formBuilder.group({
      email: new FormControl("", [Validators.required, Validators.email]),
      username: new FormControl("", [Validators.required, Validators.minLength(2), MyValidators.notBlank, Validators.pattern("^[a-zA-Z0-9-_.@#()!$]*$")]),
      password: new FormControl("", [Validators.required, Validators.minLength(2), MyValidators.notBlank]),
      confirmPassword: new FormControl("", [Validators.required, Validators.minLength(2), MyValidators.notBlank])
    }, {
      validators: MyValidators.fieldMatch("password", "confirmPassword")
    })
  }

  get email(){return this.registerFormGroup.get("email")}
  get username(){return this.registerFormGroup.get("username")}
  get password(){return this.registerFormGroup.get("password")}
  get confirmPassword(){return this.registerFormGroup.get("confirmPassword")}

  onSubmit() {
    if(this.registerFormGroup.invalid){
      this.registerFormGroup.markAllAsTouched()
      return;
    }

    let user = new RegisterDto();

    user.email = this.registerFormGroup.get("email")?.value;
    user.username = this.registerFormGroup.get("username")?.value;
    user.password = this.registerFormGroup.get("password")?.value;


    this.authService.registerUser(user).subscribe(
      res => {
        console.log(res.body)
        this.confirmDialogRef.close()
        // @ts-ignore
        this.confirmDialogRef = null
      }, err => {
        this.confirmDialogRef.componentInstance.message = "Failed to register: " + err

      }
    )
    this.infoWindow();
  }

  infoWindow(){
    this.confirmDialogRef = this.dialog.open(ConfirmationDialogComponent)

    this.confirmDialogRef.componentInstance.message = "We sent you link on email address to activate your account"

    this.confirmDialogRef.afterClosed().subscribe(res => {
      this.confirmDialogRef.close();
      // @ts-ignore
      this.confirmDialogRef = null;
    })
  }
}
