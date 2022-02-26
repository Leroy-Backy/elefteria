import { Component, OnInit } from '@angular/core';
import {UserService} from "../../services/user.service";
import {ActivatedRoute, Router} from "@angular/router";
import {User} from "../../models/User";
import {ImageService} from "../../services/image.service";
import {NgForm} from "@angular/forms";

@Component({
  selector: 'app-search-users',
  templateUrl: './search-users.component.html',
  styleUrls: ['./search-users.component.css']
})
export class SearchUsersComponent implements OnInit {

  constructor(private userService: UserService,
              private route: ActivatedRoute,
              private imageService: ImageService,
              private router: Router) { }

  //@ts-ignore
  imagesUrl: string = window["cfgApiBaseUrl"] + "/api/images/";

  users: User[] = [];
  pageNumber: number = 0;
  pageSize: number = 20;
  totalPages: number = 0;
  isKeyword: boolean = false;
  keyword!: string;
  prevKeyword: string = "";

  ngOnInit(): void {
    this.route.paramMap.subscribe(() => {
      this.handleSearch()
    })
  }

  //check if there is keyword parameter in router link
  handleSearch(){
    this.isKeyword = this.route.snapshot.paramMap.has("keyword")

    // if no, I just show all users ordered by amount of followers
    if(!this.isKeyword){
      this.getAllUsers()
    } else {
      // @ts-ignore
      this.keyword = this.route.snapshot.paramMap.get("keyword")
      if(this.keyword != this.prevKeyword){
        this.users = [];
        this.pageNumber = 0;
        this.prevKeyword = this.keyword;
      }

      this.searchUsers()
    }
  }

  searchUsers(){
    this.userService.searchUsers(this.keyword, this.pageNumber, this.pageSize).subscribe(
      data => {
        let tempUsers = data.content
        //set total pages for pagination
        this.totalPages = data.totalPages

        //make link to user profile image and push to users array
        for(let user of tempUsers){
          if(user.avatar)
            user.avatar = this.imagesUrl + user.avatar;

          this.users.push(user);
        }
      },
      err => console.log(err)
    )
  }

  getAllUsers(){
    this.userService.getAllUsers(this.pageNumber, this.pageSize).subscribe(
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

  // load more users when scrolled all loaded
  onScroll(){
    if(this.pageNumber >= this.totalPages - 1){
      //console.log("All elements was uploaded!")
    } else {
      this.pageNumber++;
      if(this.isKeyword)
        this.searchUsers()
      else
        this.getAllUsers()

    }
  }

  doSearch(form: NgForm) {
    let value = form.controls["keyword"].value

    this.router.navigateByUrl(`/search/${value}`);
  }

  goToUser(username: string) {
    this.router.navigate(["/user/" + username]).then(
      () => {location.reload()})
  }
}
