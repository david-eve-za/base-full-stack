import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private baseUrl = environment.apiUrl;

  constructor(private http:HttpClient) { }

  /**
   * Get all products
   */
  getProducts(){
    return this.http.get(`${(this.baseUrl)}/products`);
  }

  /**
   * Get product by id
   */
  getProduct(id: number){
    return this.http.get(`${(this.baseUrl)}/products/${id}`);
  }

  /**
   * Create product
   */
  createProduct(product: any){
    return this.http.post(`${(this.baseUrl)}/products`, product);
  }

  /**
   * Update product
   */
  updateProduct(id: number, product: any){
    return this.http.put(`${(this.baseUrl)}/products/${id}`, product);
  }

  /**
   * Delete product
   */
  deleteProduct(id: number){
    return this.http.delete(`${(this.baseUrl)}/products/${id}`);
  }
}
