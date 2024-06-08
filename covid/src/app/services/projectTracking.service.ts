import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { ProjectTracking } from '../models/ProjectTracking';
import { environment } from 'src/environments/environment';
import { RealEndDate } from '../models/RealEndDate';
import { ProjectProfile } from '../models/ProjectProfile';
import { ProjectHistory } from '../models/ProjectHistory';

 
@Injectable({
  providedIn: 'root'
})
export class ProjectTrackingService {

  private env = environment.apiHost + '/projectTracking';

  uploadOptions={
    1: 'Proyectos',
    2: 'Funding',
    3: 'Evaluacion'};

  constructor(private http: HttpClient) { }

  upload(option: number, file: FormData): Observable<ProjectTracking[]> {
    if (option == 1)
      return this.uploadProject(file);

    if (option == 2)
      return this.uploadFunding(file);
    if (option == 3)
      return this.uploadSurvay(file);
  }
   
  uploadProject(file:FormData):Observable<ProjectTracking[]>{
     const url = `${this.env}/massive`;
    return this.http.post<ProjectTracking[]>(url, file);
  }

  uploadFunding(file: FormData): Observable<ProjectTracking[]> {
    const url = `${this.env}/fundingmassive`;
    return this.http.post<ProjectTracking[]>(url, file);
  }

  deleteProfile(profiles:ProjectProfile[]) :Observable<any>{
    const url = `${this.env}/deleteProfile`;
    return this.http.post<any>(url, profiles);
  }

  uploadSurvay(file: FormData): Observable<ProjectTracking[]> {
    const url = `${this.env}/survaymassive`;
    return this.http.post<ProjectTracking[]>(url, file);
  }

  guardar(projectTracking: ProjectTracking): Observable<ProjectTracking[]> {
    const url = `${this.env}/create`;
    return this.http.post<ProjectTracking[]>(url, projectTracking);
  }

  getAllProjectTracking(): Observable<ProjectTracking[]> {
    const url = `${this.env}/get/all`;
    return this.http.get<ProjectTracking[]>(url);
  }

  findById(projectId: string): Observable<ProjectTracking> {
    const url = `${this.env}/find/${ projectId }`;
    return this.http.get<ProjectTracking>(url);
  }

  getChangeLogs(projectId: string): Observable<RealEndDate[]> {
    const url = `${this.env}/get/date/logs/${ projectId }`;
    return this.http.get<RealEndDate[]>(url);
  }

  getHistory(projectId: string): Observable<ProjectHistory[]> {
    const url = `${this.env}/get/history/${ projectId }`;
    return this.http.get<ProjectHistory[]>(url);
  }

  updateProjectTracking(projectId: string, projectTracking: ProjectTracking): Observable<ProjectTracking> {
    const url = `${this.env}/update/${ projectId }`;
    return this.http.put<ProjectTracking>(url, projectTracking);
  }

  deleteById(projectId: number): Observable<any> {
    const url = `${this.env}/delete/${ projectId }`;
    return this.http.delete(url);
  }
}
