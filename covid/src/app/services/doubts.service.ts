import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from 'src/environments/environment';
import { Doubt } from '../models/Doubt';

@Injectable({
    providedIn: 'root'
})
export class DoubtsService {
    headers: HttpHeaders = new HttpHeaders();
    private env = environment.apiHost + '/doubts';
  
    constructor(private _http: HttpClient) { }

    sendDoubt(doubt: Doubt): Observable<any> {
        const url =  `${this.env}/send`;
        console.log(url);
        return this._http.post<any>(url, doubt);
    }
}

