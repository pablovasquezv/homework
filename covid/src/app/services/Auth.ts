import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { LoginService } from './../login/login.service';

@Injectable()
export class BasicAuthInterceptor implements HttpInterceptor {
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        // add authorization header with basic auth credentials if available
        request = request.clone({
            setHeaders: {
                Authorization: localStorage.getItem('token'),
                usuario: localStorage.getItem('currentUser')
            }
        });

        return next.handle(request);
    }
}

@Injectable()
export class AuthGuard implements CanActivate {

    constructor(private router: Router, private _loginService: LoginService) { }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {

        // Se verifica valides de token
        if (!this._loginService.isTokenExpired(localStorage.getItem('token'))) {
            // logged in so return true
            return true;
        }

        // not logged in so redirect to login page with the return url
        this.router.navigate(['/'], { queryParams: { returnUrl: state.url }});
        return false;
    }
}
