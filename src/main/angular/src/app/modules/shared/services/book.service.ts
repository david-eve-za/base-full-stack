import {Injectable} from '@angular/core';
import {environment} from "../../../../environments/environment";
import {HttpClient, HttpHeaders} from "@angular/common/http";

const baseurl = environment.apiUrl + '/mng';

@Injectable({
  providedIn: 'root'
})
export class BookService {

  constructor(private http: HttpClient) {
  }

  /**
   * Get all books
   */
  getBooks() {
    return this.http.get(`${baseurl}/books`);
  }

  /**
   * Get book by id
   */
  getBook(id: number) {
    return this.http.get(`${baseurl}/book/${id}`);
  }

  /**
   * Get Metadata
   */
  getMetadata(id:number){
    return this.http.get(`${baseurl}/getMeta/${id}`);
  }

  /**
   * Create book
   */
  createBook(data: any) {
    const body = new URLSearchParams();
    body.set('url', data.url);
    let options = {
      headers: new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded')
    };
    return this.http.post(`${baseurl}/addBook`, body.toString(), options);
  }
}
