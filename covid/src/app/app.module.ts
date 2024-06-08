import { BrowserModule } from '@angular/platform-browser';
import { NgModule, LOCALE_ID } from '@angular/core';
//Gráficos
import { ChartsModule } from 'ng2-charts';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BaseComponent } from './base/base.component';
import { LayoutModule } from '@angular/cdk/layout';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';

import { UserListComponent } from './components/user-list/user-list.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatRadioModule } from '@angular/material/radio';
import { MatCardModule } from '@angular/material/card';
import { LoginComponent } from './login/login.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatMenuModule } from '@angular/material/menu';
import { WelcomeComponent } from './components/welcome/welcome.component';
import { UserDetailComponent } from './components/user-detail/user-detail.component';

import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { MatNativeDateModule, MAT_DATE_LOCALE } from '@angular/material/core';

import { MatMomentDateModule } from '@angular/material-moment-adapter';
import { BasicAuthInterceptor, AuthGuard } from './services/Auth';
import { ExcelService } from './components/user-list/excel.service';
import { UserListFilterService } from './components/user-list/user-list-filter.service';
import { ProjectTrackingComponent } from './components/project-tracking/project-tracking.component';
import { ExcelExportService } from './services/excelExport.service';
import { ProjectTrackingFilterService } from './components/project-tracking/project-tracking-filter.service';
import { ProjectDetailComponent } from './components/project-detail/project-detail.component';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { FindCompleteComponent } from './components/find-complete/find-complete.component';

import { PercentageDirective } from './directive/percentage.directive';
import { MinMaxDirective } from './directive/maxMin.directive';
import { HasPermissionDirective } from './directive/permissions.directive';

import { DatePipe } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { PlanContComponent } from './components/plan-cont/plan-cont.component';
import { PlanContFilterService } from './components/plan-cont/plan-cont-filter.service';
import { DetPlanContComponent } from './components/detplan-cont/detplan-cont.component';
import { AlifeFileToBase64Module } from 'alife-file-to-base64';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { registerLocaleData } from '@angular/common';
import localeCL from '@angular/common/locales/es-CL';
import localeExtraCL from '@angular/common/locales/extra/es-CL';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatExpansionModule } from '@angular/material/expansion';
import { ChangeLogComponent } from './components/project-detail/change-log/change-log.component';
import { NumericInputComponent } from './components/numeric-input/numeric-input.component'
import { AdminProfilesComponent } from './components/admin-profiles/admin-profiles.component';
import { AdminProfileAddComponent } from './components/admin-profile-add/admin-profile-add.component';
import { AdminProfileEditComponent } from './components/admin-profile-edit/admin-profile-edit.component';
import { HistoryTabComponent } from './components/project-detail/history-tab/history-tab.component';
import { GraphiBarChartComponent } from './components/project-detail/graphi-bar-chart/graphi-bar-chart.component';



// the second parameter 'fr-FR' is optional
registerLocaleData(localeCL, 'es-CL', localeExtraCL);


@NgModule({
  declarations: [
    AppComponent,
    BaseComponent,
    UserListComponent,
    LoginComponent,
    WelcomeComponent,
    UserDetailComponent,
    ProjectTrackingComponent,
    ProjectDetailComponent,
    FindCompleteComponent,
    PercentageDirective,
    MinMaxDirective,
    HasPermissionDirective,
    PlanContComponent,
    DetPlanContComponent,
    ChangeLogComponent,
    NumericInputComponent,
    AdminProfilesComponent,
    AdminProfileAddComponent,
    AdminProfileEditComponent,
    HistoryTabComponent,
    GraphiBarChartComponent,
  
  ],
  imports: [
    MatAutocompleteModule,
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    LayoutModule,
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatIconModule,
    MatListModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatSelectModule,
    MatCheckboxModule,
    MatRadioModule,
    MatCardModule,
    ReactiveFormsModule,
    MatGridListModule,
    MatMenuModule,
    MatDatepickerModule,
    HttpClientModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatMomentDateModule,
    MatTabsModule,
    AlifeFileToBase64Module,
    NgbModule,
    MatTooltipModule,
    MatExpansionModule,
    ChartsModule,
  ],
  providers: [ExcelService, ExcelExportService, UserListFilterService, ProjectTrackingFilterService, PlanContFilterService, AuthGuard, DatePipe,
    { provide: LOCALE_ID, useValue: 'es-CL' }, { provide: MAT_DATE_LOCALE, useValue: 'es-CL' },
    { provide: HTTP_INTERCEPTORS, useClass: BasicAuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {

}