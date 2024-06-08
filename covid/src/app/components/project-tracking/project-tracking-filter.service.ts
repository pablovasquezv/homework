import { Injectable } from '@angular/core';
import { ProjectTrackingFilter } from './../../models/ProjectTrackingFilter';

@Injectable()
export class ProjectTrackingFilterService {

  filterProjectTracking: ProjectTrackingFilter=new ProjectTrackingFilter("","","","","","","","","",null,null);
  constructor() { }

  setFilters(filterProjectTracking: ProjectTrackingFilter){
    this.filterProjectTracking = filterProjectTracking;
  }

  getFilters(){
    return this.filterProjectTracking;
  }
}