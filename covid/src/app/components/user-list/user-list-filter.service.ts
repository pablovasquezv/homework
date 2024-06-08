import { Injectable } from '@angular/core';
import { UserListFilter } from './../../models/UserListFilter';

@Injectable()
export class UserListFilterService {

  filterUserList: UserListFilter;
  constructor() { }

  setFilters(userListFilter: UserListFilter){
    this.filterUserList = userListFilter;
  }

  getFilters(){
    return this.filterUserList;
  }
}