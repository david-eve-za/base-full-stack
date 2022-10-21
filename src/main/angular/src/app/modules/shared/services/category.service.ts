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

  /**
   * Get category by id
   */
  getCategoryById(id: number){
    return this.http.get(`${baseurl}/categories/${id}`);
  }

  /**
   * Create category
   */
  createCategory(category: any){
    return this.http.post(`${baseurl}/categories`, category);
  }

  /**
   * Update category
   */
  updateCategory(id: number, category: any){
    return this.http.put(`${baseurl}/categories/${id}`, category);
  }

  /**
   * Delete category
   */
  deleteCategory(id: number){
    return this.http.delete(`${baseurl}/categories/${id}`);
  }
}
