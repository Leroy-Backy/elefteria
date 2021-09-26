import {Component, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {MyValidators} from "../../validators/MyValidators";
import {MatDialogRef} from "@angular/material/dialog";
import {PostService} from "../../services/post.service";
import {Poll} from "../../models/Poll";
import {PollOption} from "../../models/PollOption";
import {NgxSpinnerService} from "ngx-spinner";

@Component({
  selector: 'app-create-post',
  templateUrl: './create-post.component.html',
  styleUrls: ['./create-post.component.css']
})
export class CreatePostComponent implements OnInit {

  postForm!: FormGroup;
  images: File[] = [];

  constructor(private formBuilder: FormBuilder,
              private dialogRef: MatDialogRef<CreatePostComponent>,
              private postService: PostService,
              private spinner: NgxSpinnerService) { }

  ngOnInit(): void {
    this.postForm = this.formBuilder.group({
      title: new FormControl(""),
      text: new FormControl(""),
      file: new FormControl(),
      poll_question: new FormControl(""),
      poll: new FormArray([])
    })
  }

  get text(){return this.postForm.get("text")}
  get title(){return this.postForm.get("title")}
  get poll_question(){return this.postForm.get("poll_question")}
  get poll(){return this.postForm.get("poll") as FormArray}

  onSubmit(){
    if(this.postForm.invalid){
      this.postForm.markAllAsTouched();
      return;
    }

    let title = this.postForm.get("title")?.value
    let text = this.postForm.get("text")?.value

    let poll: Poll|null = null;

    if(this.poll.length > 0){
      poll = new Poll(this.postForm.get("poll_question")?.value)

      poll.options = []

      for(let i = 0; i < this.poll.length; i++){

        let option = new PollOption(this.poll.controls[i].value)

        poll.options.push(option)
      }
    }

    this.postService.createPost(title, text, this.images, poll).subscribe(
      response => {
        this.spinner.hide();
        this.postForm.reset();
        //say to post service to load new post and give it to user component
        this.postService.getLastPost();
        this.dialogRef.close();
      }
    )

    this.spinner.show();

    this.postForm.reset();
    this.images = [];
  }

  addPoll(){
    this.addPollOption()
    this.addPollOption()
  }

  addPollOption(){
    //@ts-ignore
    this.postForm.get("poll").push(new FormControl("", [Validators.required, Validators.minLength(2), Validators.maxLength(125), MyValidators.notBlank]))
  }

  removePollOption(i: number){
    //@ts-ignore
    this.postForm.get("poll").removeAt(i);
  }

  onClose() {
    this.postForm.reset();
    this.dialogRef.close();
    this.images = [];
  }

  addImages(event: Event) {
    if((<HTMLInputElement>event.target).files != null){
      // @ts-ignore
      let filesAmount: number = (<HTMLInputElement>event.target).files?.length;

      for(let i = 0; i < filesAmount; i++){
        // @ts-ignore
        this.images.push((<HTMLInputElement>event.target).files[i])
      }
    }
  }

}
