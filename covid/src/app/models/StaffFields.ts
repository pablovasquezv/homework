import { ProjectProfile } from './ProjectProfile';

export class StaffFields{

    sumTarifas: number;
     /**
     * Tipo de tarifa
	 * 0 = Tarifa fija mensual
	 * 1 = Tarifa por hora
	 */
    rateType: number;
    detailProjectProfileList: ProjectProfile[];

    constructor(){
        this.detailProjectProfileList=[];
    }
}