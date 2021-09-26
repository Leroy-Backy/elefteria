import { Component, OnInit } from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {MyValidators} from "../../validators/MyValidators";
import {ConfirmationDialogComponent} from "../confirmation-dialog/confirmation-dialog.component";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-change-password-confirm',
  templateUrl: './change-password-confirm.component.html',
  styleUrls: ['./change-password-confirm.component.css']
})
export class ChangePasswordConfirmComponent implements OnInit {

  constructor(private authService: AuthService,
              private formBuilder: FormBuilder,
              private dialog: MatDialog,
              private route: ActivatedRoute,
              private router: Router) { }

  changePasswordFormGroup!: FormGroup;
  confirmDialogRef!: MatDialogRef<ConfirmationDialogComponent>;

  error: boolean = false;
  token!: string;

  ngOnInit(): void {
    this.route.paramMap.subscribe(() => {
      this.handle()
    })

    this.changePasswordFormGroup = this.formBuilder.group({
      password: new FormControl("", [Validators.required, Validators.minLength(2), MyValidators.notBlank]),
      confirmPassword: new FormControl("", [Validators.required, Validators.minLength(2), MyValidators.notBlank])
    }, {
      // Validator to check if password match
      validators: MyValidators.fieldMatch("password", "confirmPassword")
    })
  }

  private handle() {
    if(!this.route.snapshot.paramMap.has("token")){
      this.error = true;
      return;
    }

    //get token from parameter
    // @ts-ignore
    this.token = this.route.snapshot.paramMap.get("token")
  }

  get password(){return this.changePasswordFormGroup.get("password")}
  get confirmPassword(){return this.changePasswordFormGroup.get("confirmPassword")}


  onSubmit(){
    if(this.changePasswordFormGroup.invalid){
      this.changePasswordFormGroup.markAllAsTouched()
      return;
    }

    this.authService.changePassword(this.changePasswordFormGroup.get("password")?.value, this.token).subscribe(
      res => {

      }, err => {
        // if there is error with changing password I just change dialog message
        this.confirmDialogRef.componentInstance.message = "Failed: " + err
      }
    )

    this.infoWindow();
  }

  infoWindow(){
    this.confirmDialogRef = this.dialog.open(ConfirmationDialogComponent)

    this.confirmDialogRef.componentInstance.message = "You successfully changed password"

    this.confirmDialogRef.afterClosed().subscribe(res => {
      this.confirmDialogRef.close();
      // @ts-ignore
      this.confirmDialogRef = null;
      this.router.navigateByUrl("/login")
    })
  }
}
