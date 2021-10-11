import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Post} from "../models/Post";
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {Poll} from "../models/Poll";

@Injectable({
  providedIn: 'root'
})
export class PostService {

  // @ts-ignore
  private postsUrl: string = window["cfgApiBaseUrl"] + "/api/posts"

  public lastPost: Subject<Post> = new Subject<Post>();

  constructor(private httpClient: HttpClient) { }

  getLastPost(){
    let url = this.postsUrl + "/last";

    this.httpClient.get<Post>(url).subscribe(data => {
      this.lastPost.next(data)
    })
  }

  getPostById(postId: number): Observable<Post>{
    return this.httpClient.get<Post>(`${this.postsUrl}/${postId}`)
  }

  voteInPoll(pollId: number, optionNumber: number){
    const url = `${this.postsUrl}/poll/${pollId}?optionId=${optionNumber}`

    return this.httpClient.get(url, {observe: "response"})
  }

  getPostsFeedPaginate(page: number, size: number): Observable<PostPaginationResponse>{
    const url = `${this.postsUrl}/feed?page=${page}&size=${size}`;

    return this.httpClient.get<PostPaginationResponse>(url);
  }

  getPostsByUserIdPaginate(id: number, page: number, size: number): Observable<PostPaginationResponse>{
    const url = `${this.postsUrl}/user/${id}?page=${page}&size=${size}`;

    return this.httpClient.get<PostPaginationResponse>(url);
  }

  likePost(id: number){
    this.httpClient.post(`${this.postsUrl}/${id}/like`, {}, {observe: "response"}).subscribe(
      response => {}, error => { console.log(error) }
    )
  }

  getPopularPosts(page: number, size: number): Observable<PostPaginationResponse>{
    const url = `${this.postsUrl}/popular?page=${page}&size=${size}`;

    return this.httpClient.get<PostPaginationResponse>(url)
  }

  createPost(title: string, text: string, images: File[], poll: Poll | null){
    const formData = new FormData();
    formData.append("title", title);
    formData.append("text", text)
    if(poll){
      formData.append("pollString", JSON.stringify(poll));
    }

    for(let file of images){
      formData.append("files", file);
    }

    return this.httpClient.post(this.postsUrl, formData, {observe: "response"})
  }

  deletePost(id: number){
    this.httpClient.delete(`${this.postsUrl}/${id}`, {observe: "response"}).subscribe(
      response => console.log(response.body),
      error => console.log(error.message)
    )
  }

  getLikesByPostId(postId: number): Observable<String[]>{
    let url = `${this.postsUrl}/likes/${postId}`

    return this.httpClient.get<String[]>(url)
  }

}

interface PostPaginationResponse{
  content: Post[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
