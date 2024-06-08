import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http'
import { Usuario } from '../models/usuario';
import { Observable } from 'rxjs';
import * as jwt_decode from 'jwt-decode';
import { environment } from 'src/environments/environment';


@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private base = environment.apiHost + '/user/';

  private endpoint = 'login'
  constructor(private _http: HttpClient) { }

  
  //cambiar a string luego de pruebas
  login(usersad: string, pass: string): Observable<Usuario> {
    return this._http.post<Usuario>( this.base + this.endpoint, {
      username: usersad,
      password: pass
    });
  }

  setUser(usuario: Usuario) {
    localStorage.setItem("currentUser", usuario.username);
    localStorage.setItem("employeeNumber", usuario.employeeNumber);
    localStorage.setItem("token", usuario.token);
  }

  getTokenExpirationDate(token: string): Date {
    const decoded = jwt_decode(token);
    if (decoded.exp === undefined) return null;
    const date = new Date(0);
    date.setUTCSeconds(decoded.exp);
    return date;
  }

  isTokenExpired(token?: string): boolean {
    if(!token) token = localStorage.getItem('token');
    if(!token) return true;

    const date = this.getTokenExpirationDate(token);
    if(date === undefined) return false;
    return !(date.valueOf() > new Date().valueOf());
  }

  getTokenRole(token: string): string{
    const decoded = jwt_decode(token);
    if (decoded.authorities === undefined) return null;
    return decoded.authorities;
  }

  isTokenAdmin(token?: string): boolean{
    if(!token) token = localStorage.getItem('token');
    if(!token) return true;

    const role = this.getTokenRole(token);
    if(role === undefined) return false;
    if(role === 'ROLE_ADMIN') return true;
    return false;
  }
}
