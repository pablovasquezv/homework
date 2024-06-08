import { Directive,  Input,  OnInit, ElementRef, TemplateRef, ViewContainerRef } from '@angular/core';
import { Profile } from '../models/Profile';
import { ProfileService } from '../services/profile.service';
import { Router } from '@angular/router';
import { Rol } from '../models/Rol';

@Directive({
  selector: '[hasPermission]'
})
export class HasPermissionDirective implements OnInit {
  private profile:Profile;
  private permissions = [];
  private domain:string="";

  constructor(
    private element: ElementRef,
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private profileService: ProfileService,
    private router: Router
  ) {
  }

  ngOnInit() {
    this.loadProfile();
  }

  
  @Input()
  set hasPermission(val) {
    this.permissions = val;
    this.updateView();
  }

  
  @Input()
  set hasPermissionDomain(val) {
    this.domain = val;
    this.updateView();
  }
  loadProfile() {
    let user = localStorage.getItem("currentUser");
    console.log("Domain",this.domain);
    console.log("USER",user);

    if (user == null || user == '') return;
    this.profileService.getFilteredProfileFor(user, this.domain).subscribe(
      (res) => {
        this.profile = <Profile>(res);
        if (res != null) {
          Object.setPrototypeOf(this.profile, Profile.prototype);
          this.profile.setRolesNature();
        }
        else {
          this.profile = new Profile();
          this.profile.user = user;
          this.profile.roles = [];
        }
        //si no hay roles, cargo el 'DEFAULT'
        if (this.profile.roles.length == 0) {
          let rol = new Rol();
          rol.domain = this.domain;
          rol.name = "Default";
          this.profile.roles = [rol];
        }
        console.log(res);
        this.updateView();
      },
      (err) => {
        console.log(err);
        this.router.navigate(['//']);
      });
  }

  private updateView() {
    if (this.checkPermission()) {
        this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }
  }

  private checkPermission() {
    let hasPermission = false;

    if (this.profile != null && this.profile.roles != null){
      for (const checkPermission of this.permissions) {
        const permissionFound = this.profile.roles.find(x => x.name.toUpperCase() === checkPermission.toUpperCase());
        hasPermission = (hasPermission || (permissionFound!=null) );
      }
    }

    return hasPermission;
  }
}
