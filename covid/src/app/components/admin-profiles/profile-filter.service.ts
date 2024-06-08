import { Injectable } from '@angular/core';
import { ProfileFilter } from '../../models/ProfileFilter';

@Injectable()
export class ProfileFilterService {

    filterPorfile: ProfileFilter = new ProfileFilter("");
    constructor() { }

    setFilters(filterPorfile: ProfileFilter) {
        this.filterPorfile = filterPorfile;
    }

    getFilters() {
        return this.filterPorfile;
    }
}