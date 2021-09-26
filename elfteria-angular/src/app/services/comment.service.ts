import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Comment} from "../models/Comment";

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  constructor(private httpClient: HttpClient) { }

  // private commentUrl: string = "http://localhost:8080/api/comments"
  // @ts-ignore
  private commentUrl: string = window["cfgApiBaseUrl"] + "/api/comments"

  getCommentsByPostId(id: number): Observable<Comment[]>{
    return this.httpClient.get<Comment[]>(`${this.commentUrl}/post/${id}`)
  }

  createComment(id: number, text: string){
    return this.httpClient.post(`${this.commentUrl}/post/${id}`, {"text": text}, {observe: "response"})
  }
}
