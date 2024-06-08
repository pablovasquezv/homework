import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from 'src/environments/environment';
import { Profile } from '../models/Profile';

@Injectable({
    providedIn: 'root'
})
export class UserService {
    headers: HttpHeaders = new HttpHeaders();

    private base = environment.apiHost + '/user/';
    constructor(private _http: HttpClient) { }

    findByUsername(username: string): Observable<Profile[]> {
        const url = `${this.base}get/by/${username}`;
        return this._http.get<Profile[]>(url);
    }

    createUser(user: Profile): Observable<Profile[]> {
        const url = `${this.base}create/`;
        return this._http.post<Profile[]>(url, user);
    }

    updateUser(user: Profile): Observable<Profile[]> {
        const url = `${this.base}create/`;
        return this._http.post<Profile[]>(url, user);
    }

    deleteUser(username: string, rol:number): Observable<any> {
        const url = `${this.base}delete/${username}/${rol}`;
        return this._http.delete(url);
    }

}