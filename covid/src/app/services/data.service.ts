import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Homework } from './../models/Homework';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DataService {
  headers: HttpHeaders = new HttpHeaders();
  private arrayHome: Array<Homework>;

  private base = environment.apiHost + '/homework/';
  private endpoint = 'get/all';
  constructor(private _http: HttpClient) { }

  getAll(): Observable<Homework[]> {
    return this._http.get<Homework[]>( this.base + this.endpoint);
  }
}
