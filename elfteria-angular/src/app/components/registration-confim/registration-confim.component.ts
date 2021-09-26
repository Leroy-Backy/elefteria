import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-registration-confim',
  templateUrl: './registration-confim.component.html',
  styleUrls: ['./registration-confim.component.css']
})
export class RegistrationConfimComponent implements OnInit {

  constructor(private router: Router,
              private userService: UserService,
              private route: ActivatedRoute) { }

  activated: boolean = false;
  error: boolean = false;
  audio = new Audio("assets/audio/basy-tektonik.mp3")

  ngOnInit(): void {
    this.route.paramMap.subscribe(() => {
      this.activateUser()
    })
  }

  activateUser(){
    let isKeyword: boolean = this.route.snapshot.paramMap.has("token")

    if(!isKeyword){
      this.error = true;
      return
    }

    // @ts-ignore
    let token: string = this.route.snapshot.paramMap.get("token")

    this.userService.activateAccount(token).subscribe(
      res => {
        if(res.status == 200)
          this.activated = true
      },
      err => {
        console.log(err.message)
        this.error = true
      }
    )
  }

  playAudio(){
    this.audio.load();
    this.audio.volume = 0.05;
    this.audio.play();
  }

  toLogin(){
    this.audio.pause()
    this.router.navigateByUrl("/login")
  }
}
