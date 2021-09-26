import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class ImageService {
  // @ts-ignore
  private imagesUrl: string = window["cfgApiBaseUrl"] + "/api/images"

  constructor(private httpClient: HttpClient) { }

  uploadAvatar(imageData: FormData){
    return this.httpClient.post(this.imagesUrl + "/avatar", imageData, {observe: "response"})
  }
}
