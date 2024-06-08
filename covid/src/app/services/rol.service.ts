import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Rol } from '../models/Rol';

import { environment } from 'src/environments/environment';

@Injectable({
    providedIn: 'root'
})
export class RolService {
    headers: HttpHeaders = new HttpHeaders();

    private base = environment.apiHost + '/rol/';
    constructor(private _http: HttpClient) { }

    getAllRols(): Observable<Rol[]> {
        const url = `${this.base}get/all`;
        return this._http.get<Rol[]>(url);
    }
}