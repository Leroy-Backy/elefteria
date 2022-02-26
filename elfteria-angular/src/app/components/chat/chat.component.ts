import {AfterViewChecked, AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ChatService} from "../../services/chat.service";
import {ChatMessage} from "../../models/ChatMessage";
import {NgForm} from "@angular/forms";
import {AuthService} from "../../services/auth.service";
import {User} from "../../models/User";
import {StaticMethods} from "../../models/StaticMethods";
import {WebSocketMessage} from "../../models/WebSocketMessage";

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit, AfterViewChecked, AfterViewInit, OnDestroy {
  //@ts-ignore
  @ViewChild('scrollMe') myScrollContainer: ElementRef

  constructor(private chatService: ChatService,
              private authService: AuthService) { }

  messages: ChatMessage[] = [];

  pageNumber: number = 0;
  pageSize: number = 30;
  totalPages: number = 0;
  check: boolean = false;
  user: User = new User();

  image!: File;

  //@ts-ignore
  imagesUrl: string = window["cfgApiBaseUrl"] + "/api/images/";

  ngAfterViewInit() {
    this.scrollToBottom()
  }

  ngOnInit(): void {
    this.getMessages()

    // connect to web-socket
    this.chatService.connect()

    this.authService.getCurrentUser().subscribe(data => {
      this.user = data
    })

    /* subscribe to web socket messages. When user send message with image
       web socket message has no nickname field, only content "image"
       so when this method receive that message it make request to server for new messages.
       But when there is only text this method creates new message object from info sent through web-socket
       It allows to send text messages much faster
     */
    this.chatService.webSocketMessage.subscribe(data => {
      //check if it is text message
      if(data.senderUsername){
        let msg: ChatMessage = new ChatMessage();
        msg.id = this.messages[this.messages.length-1].id + 1;
        msg.createdDate = new Date(Date.now());
        msg.message = data.content;
        msg.user = data.senderUsername;
        this.messages.push(msg)

      } else if(data.content !== ""){
        //if it has image just get it from a server
        this.getNewMessages(this.messages[this.messages.length-1].id)
      }
    })
  }

  ngOnDestroy() {
    this.chatService.disconnect()
  }

  ngAfterViewChecked() {
    if(!this.check)
      this.scrollToBottom()
  }

  scrollToBottom(): void {
    try {
      this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
    } catch(err) { }
  }

  sendMessage(sendForm: NgForm){
    const formData = new FormData();
    formData.append("user", this.user.username);
    formData.append("message", sendForm.value.message);

    let msg: WebSocketMessage = new WebSocketMessage("image")

    if(this.image) {
      formData.append("file", this.image);
    } else {
      // if there is no image I set nickname and message
      msg.content = sendForm.value.message;
      msg.senderUsername = this.user.username;
    }

    this.chatService.sendMessage(formData, msg);

    sendForm.control.controls["message"].reset();
    //@ts-ignore
    this.image = null
  }

  setImage(event: Event) {
    if((<HTMLInputElement>event.target).files![0]) {
      this.image = (<HTMLInputElement>event.target).files![0];

      //@ts-ignore
      (<HTMLInputElement>event.target).value = null;
    }
  }

  getMessages(){
    this.chatService.getMessagesPaginate(this.pageNumber, this.pageSize).subscribe(
      data => {
        this.totalPages = data.totalPages;

        for(let msg of data.content) {
          msg.createdDate = StaticMethods.formatToDate(msg.createdDate)

          if(msg.image){
            msg.image = `${this.imagesUrl}${msg.image}`
          }

          this.messages.unshift(msg)
        }
      }
    )
  }

  getNewMessages(afterId: number){
    this.chatService.getNewMessages(afterId).subscribe(data => {
      for(let msg of data){
        msg.createdDate = StaticMethods.formatToDate(msg.createdDate)

        if(msg.image){
          msg.image = `${this.imagesUrl}${msg.image}`
        }

        this.messages.push(msg)
      }
    })
  }

  onScroll(){
    //console.log("scrolled up")
    if(this.pageNumber >= this.totalPages - 1){
      //console.log("All elements was uploaded!")
    } else {
      this.pageNumber++;
      this.check = true;
      this.getMessages();
    }
  }
}
