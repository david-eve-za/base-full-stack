import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../environments/environment";

const baseurl = environment.apiUrl;

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  constructor(private http:HttpClient) { }

  /**
   * Get all categories
   * @returns {Observable<any>}
   */
  getCategories(){
    return this.http.get(`${baseurl}/categories`);
  }
}
