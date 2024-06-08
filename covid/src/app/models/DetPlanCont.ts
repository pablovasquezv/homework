export class DetPlanCont {
	id_det_recursos_claves: number;
	perfilClave: string;
	nombrePerfilClave: string;
	nombreBackup: string;
	planContingencia: number;
	backupActividades: number;
	backupHerramientas: number;
	pclaveReunionSeg: number;
	comunicacionCliEquipo: number;
	comentarios: string;
	projectCode: string;

	constructor(projectCode: string="") {
        this.projectCode=projectCode;
	}
}