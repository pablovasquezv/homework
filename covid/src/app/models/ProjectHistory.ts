import { Modification } from './Modification';
import { TraditionalFields } from './TraditionalFields';
import { ServiceFields } from './ServiceFields';
import { StaffFields } from './StaffFields';
import { TechnicalFields } from './TechnicalFields';

export class ProjectHistory {
    id: number;
    projectCode: string;
    lastModified:Date;
    username:string;
    traditional: TraditionalFields;
    service: ServiceFields;
    staff: StaffFields;
    technical: TechnicalFields;

    constructor() { }
}