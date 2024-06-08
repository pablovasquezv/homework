import { Component, OnInit } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { environment } from './../../environments/environment';
import { Router } from '@angular/router';




@Component({
  selector: 'app-base',
  templateUrl: './base.component.html',
  styleUrls: ['./base.component.scss']
})
export class BaseComponent implements OnInit {
  onlyCL: boolean = false;

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

  constructor(private breakpointObserver: BreakpointObserver,
    private router: Router) { }

  ngOnInit() {
    if (environment.location) {
      console.log('LOCATION: ' + environment.location);
      this.onlyCL = false;
      if (environment.location == 'DEV' || environment.location == 'CL') {
        this.onlyCL = true;
      }
    }
  }

  salir() {
    localStorage.setItem("currentUser", '');
    localStorage.setItem("token", '');
    localStorage.setItem("userlist_pageSize",'');
    localStorage.setItem("tracking_pageSize",'');
    localStorage.setItem("plancontlist_pageSize",'');
    localStorage.setItem("detplancontlist_pageSize",'');
  }

}
