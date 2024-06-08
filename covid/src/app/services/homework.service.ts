import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Homework } from '../models/Homework';
import { environment } from 'src/environments/environment';
 
@Injectable({
  providedIn: 'root'
})
export class HomeworkService {

  private env = environment.apiHost + '/homework';

  constructor(private http: HttpClient) { }

  upload(file: FormData): Observable<Homework[]> {
    const url = `${this.env}/massive`;
    return this.http.post<Homework[]>(url, file);
  }

  guardar(homework: Homework): Observable<Homework[]> {
    const url = `${this.env}/create`;
    return this.http.post<Homework[]>(url, homework);
  }

  getAllHomework(): Observable<Homework[]> {
    const url = `${this.env}/get/all`;
    return this.http.get<Homework[]>(url);
  }

  findById(userId: string): Observable<Homework> {
    const url = `${this.env}/find/${ userId }`;
    return this.http.get<Homework>(url);
  }

  findByUser(username: string): Observable<Homework> {
    const url = `${this.env}/findByUser/${ username }`;
    return this.http.get<Homework>(url);
  }

  updateHomework(userId: number, homework: Homework): Observable<Homework> {
    const url = `${this.env}/update/${ userId }`;
    return this.http.put<Homework>(url, homework);
  }

  deleteById(userId: number): Observable<any> {
    const url = `${this.env}/delete/${ userId }`;
    return this.http.delete(url);
  }
}
