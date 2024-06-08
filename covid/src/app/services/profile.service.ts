import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Profile } from '../models/Profile';

import { environment } from 'src/environments/environment';

@Injectable({
    providedIn: 'root'
})
export class ProfileService {
    headers: HttpHeaders = new HttpHeaders();

    private base = environment.apiHost + '/profile/';
    constructor(private _http: HttpClient) { }

    getRawProfileFor(username: string): Observable<Profile> {
        return this._http.get<Profile>(this.base + "raw/" + username);
    }
    getFilteredProfileFor(username: string, domain: string): Observable<Profile> {
        return this._http.get<Profile>(this.base + "filtered/" + username + "/" + domain);
    }
    getAllProfiles(): Observable<Profile[]> {
        const url = `${this.base}get/all`;
        return this._http.get<Profile[]>(url);
    }
}

