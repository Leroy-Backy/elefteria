import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ChatMessage} from "../models/ChatMessage";
import {BehaviorSubject, Observable} from "rxjs";
import {CompatClient, Stomp} from "@stomp/stompjs";
import * as SockJS from "sockjs-client";
import {WebSocketMessage} from "../models/WebSocketMessage";

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  //@ts-ignore
  private messagesUrl: string = window["cfgApiBaseUrl"] + "/api/chatmessages"

  //@ts-ignore
  private chatWebsocketUrl: string = window["cfgApiBaseUrl"] + "/ws";

  private stompClient!: CompatClient;

  //Behavior Subject to store web socket messages, and chat component can subscribe and get that messages from service
  webSocketMessage: BehaviorSubject<WebSocketMessage> = new BehaviorSubject<WebSocketMessage>(new WebSocketMessage(""));

  constructor(private httpClient: HttpClient) { }

  connect(){
    const socket = new SockJS(this.chatWebsocketUrl)
    this.stompClient = Stomp.over(socket)

    const _this = this;

    //@ts-ignore
    this.stompClient.connect({"Authorization": localStorage.getItem("jwtToken")}, function (frame) {
      //console.log("Connected: " + frame)

      _this.stompClient.subscribe("/public-chat/messages", function (msg){
        _this.webSocketMessage.next(JSON.parse(msg.body))
      })
    })
  }

  disconnect(){
    if(this.stompClient){
      this.stompClient.disconnect();
      //console.log("Disconnected")
    }
  }

  sendWebSocketMessage(message: WebSocketMessage){
    this.stompClient.send("/elefteria/public-message", {}, JSON.stringify(message))
  }

  public sendMessage(formData: FormData, msg: WebSocketMessage){
    //send message to store in database
    this.httpClient.post(this.messagesUrl, formData, {observe: "response"}).subscribe(
      res => {
        //after it saved I make signal to other users that are in chat to load it
        if(!msg.senderUsername)
          this.sendWebSocketMessage(msg);
      }
    )

    //if there is no image I send whole message immediately
    if(msg.senderUsername)
      this.sendWebSocketMessage(msg);
  }

  getNewMessages(afterId: number): Observable<ChatMessage[]>{
    let url = `${this.messagesUrl}/after/${afterId}`

    return this.httpClient.get<ChatMessage[]>(url)
  }

  getMessagesPaginate(page: number, size: number): Observable<MessagePaginationResponse>{
    let url = `${this.messagesUrl}?page=${page}&size=${size}`;

    return this.httpClient.get<MessagePaginationResponse>(url);
  }

}
interface MessagePaginationResponse{
  content: ChatMessage[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
