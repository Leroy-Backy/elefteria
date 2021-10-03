import { Component, OnInit } from '@angular/core';
import {ImageService} from "../../services/image.service";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../services/auth.service";
import {User} from "../../models/User";
import {MyValidators} from "../../validators/MyValidators";
import {UserService} from "../../services/user.service";
import {MatDialogRef} from "@angular/material/dialog";
import {base64ToFile, ImageCroppedEvent, LoadedImage} from "ngx-image-cropper";
import {NgxSpinnerService} from "ngx-spinner";

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css']
})
export class EditProfileComponent implements OnInit {

  imageChangedEvent: any = '';
  croppedImage: any = '';
  imageFile!: File;

  user: User = new User();

  editFormGroup!: FormGroup;

  constructor(private imageService: ImageService,
              private formBuilder: FormBuilder,
              private authService: AuthService,
              private userService: UserService,
              private dialogRef: MatDialogRef<EditProfileComponent>,
              private spinner: NgxSpinnerService) { }

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe(
      data => this.user = data,
      err => console.log(err)
    )

    this.editFormGroup = this.formBuilder.group({
      firstName: new FormControl("", [Validators.required, Validators.minLength(2), Validators.maxLength(50), MyValidators.notBlank]),
      lastName: new FormControl("", [Validators.required, Validators.minLength(2), Validators.maxLength(50), MyValidators.notBlank]),
      // status: new FormControl("", [Validators.required, Validators.minLength(2), Validators.maxLength(255), MyValidators.notBlank]),
      status: new FormControl("")
    })
  }

  get firstName(){return this.editFormGroup.get("firstName")}
  get lastName(){return this.editFormGroup.get("lastName")}
  get status(){return this.editFormGroup.get("status")}

  onSubmit(){
    if(this.editFormGroup.invalid){
      this.editFormGroup.markAllAsTouched()
      return
    }
    this.user.firstName = this.editFormGroup.get("firstName")?.value
    this.user.lastName = this.editFormGroup.get("lastName")?.value
    this.user.status = this.editFormGroup.get("status")?.value

    this.userService.editUser(this.user).subscribe(res => {
      this.onClose()
    }, err => {
      console.log(err)
      this.onClose()
    })

    this.spinner.show()

    this.editFormGroup.reset();

  }

  onClose(){
    this.spinner.hide()
    this.editFormGroup.reset();
    location.reload()
    this.dialogRef.close();
  }

  fileChangeEvent(event: any) {
    this.imageChangedEvent = event;
  }

  imageCropped(event: ImageCroppedEvent) {
    this.croppedImage = event.base64;

    let imageFile = base64ToFile(this.croppedImage)

    this.imageFile = new File([imageFile],
      this.imageChangedEvent.target.files[0].name,
      {type: imageFile.type})

  }

  imageLoaded(event: LoadedImage) {

  }

  cropperReady() {
    //console.log("cropperReady")
  }

  loadImageFailed() {
    console.log("loadImageFailed")
  }

  onUpload(){
    if(this.imageFile) {
      const uploadImageData = new FormData();
      uploadImageData.append("file", this.imageFile)

      this.imageService.uploadAvatar(uploadImageData).subscribe(res => {
        this.onClose()
      }, err => {
        console.log(err)
        this.onClose()
      })
      this.spinner.show()

      this.imageChangedEvent = null;

    }
  }

}
