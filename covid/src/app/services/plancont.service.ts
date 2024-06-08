import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { PlanCont } from '../models/PlanCont';
import { ContDocument } from '../models/ContDocument';
import { DetPlanCont } from '../models/DetPlanCont';
 
@Injectable({
  providedIn: 'root'
})
export class PlanContService {

  private env = environment.apiHost + '/keysresources';

  constructor(private http: HttpClient) { }

   upload(file: FormData): Observable<PlanCont[]> {
    const url = `${this.env}/massive`;
    return this.http.post<PlanCont[]>(url, file);
  } 

  deleteDetail(details:DetPlanCont[]) :Observable<any>{
    const url = `${this.env}/deleteDetail`;
    return this.http.post<any>(url, details);
  }

  guardar(planCont: PlanCont ): Observable<PlanCont[]> {
    const url = `${this.env}/create`;
    return this.http.post<PlanCont[]>(url, planCont);
  }

  loadDocument(codProy:string , plan: ContDocument): Observable<PlanCont> {
    const url = `${this.env}/loadDocument/${ codProy }`;
    return this.http.post<PlanCont>(url, plan);
  }

  getDocument(codProy:string):Observable<ContDocument>{
    const url = `${this.env}/getDocument/${ codProy }`;
    return this.http.get<ContDocument>(url);
  }

  getAllPlanCont(): Observable<PlanCont[]> {
    const url = `${this.env}/get/all`;
    return this.http.get<PlanCont[]>(url);
  }

  findById(codProd: string): Observable<PlanCont> {
    const url = `${this.env}/find/${ codProd }`;
    return this.http.get<PlanCont>(url);
  }

/*   updateHomework(userId: number, homework: Homework): Observable<Homework> {
    const url = `${this.env}/update/${ userId }`;
    return this.http.put<Homework>(url, homework);
  } */

  deleteById(projectCode: string): Observable<any> {
    const url = `${this.env}/delete/${ projectCode }`;
    return this.http.delete(url);
  }
}
