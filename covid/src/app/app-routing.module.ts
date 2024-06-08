import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { BaseComponent } from './base/base.component';
import { UserListComponent } from './components/user-list/user-list.component';
import { WelcomeComponent } from './components/welcome/welcome.component';
import { UserDetailComponent } from './components/user-detail/user-detail.component';
import { ProjectTrackingComponent } from './components/project-tracking/project-tracking.component';
import { AdminProfilesComponent } from './components/admin-profiles/admin-profiles.component';
import { AdminProfileAddComponent } from './components/admin-profile-add/admin-profile-add.component';
import { AdminProfileEditComponent } from './components/admin-profile-edit/admin-profile-edit.component';
import { ProjectDetailComponent } from './components/project-detail/project-detail.component';
import { AuthGuard } from './services/Auth';
import { PlanContComponent } from './components/plan-cont/plan-cont.component';
import { DetPlanContComponent } from './components/detplan-cont/detplan-cont.component';

const routes: Routes = [
  { path: '', component: LoginComponent },
  {
    path: 'app', component: BaseComponent, children: [
      { path: '', component: WelcomeComponent, canActivate: [AuthGuard] },
      { path: 'user-list', component: UserListComponent, canActivate: [AuthGuard] },
      { path: 'user-detail', component: UserDetailComponent, canActivate: [AuthGuard] },
      { path: 'edit/:usuario', component: UserDetailComponent, canActivate: [AuthGuard] },
      { path: 'project-tracking', component: ProjectTrackingComponent, canActivate: [AuthGuard] },
      { path: 'admin-profiles', component: AdminProfilesComponent, canActivate: [AuthGuard] },
      { path: 'admin-profile-add', component: AdminProfileAddComponent, canActivate: [AuthGuard] },
      { path: 'admin-profile-edit/:usuario', component: AdminProfileEditComponent, canActivate: [AuthGuard] },
      { path: 'editProject/:project', component: ProjectDetailComponent, canActivate: [AuthGuard] },
      { path: 'plan-cont', component: PlanContComponent, canActivate: [AuthGuard] },
      { path: 'editPlanCont/:codprod', component: DetPlanContComponent, canActivate: [AuthGuard] },
    ]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
