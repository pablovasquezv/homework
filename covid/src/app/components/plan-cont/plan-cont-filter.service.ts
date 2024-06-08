import { Injectable } from '@angular/core';
import { UserListFilter } from '../../models/UserListFilter';
import { PlanContFilter } from 'src/app/models/PlanContFilter';

@Injectable()
export class PlanContFilterService {

  filterPlanContList: PlanContFilter;
  constructor() { }

  setFilters(filterPlanContList: PlanContFilter){
    this.filterPlanContList = filterPlanContList;
  }

  getFilters(){
    return this.filterPlanContList;
  }
}