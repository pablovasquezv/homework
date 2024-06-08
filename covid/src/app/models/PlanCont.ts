import { DetPlanCont } from './DetPlanCont';

export class PlanCont {
	projectCode: string;
	nombreProyecto: string;
	un: string;
	sector: string;
	ce: string;
	nombreCliente: string;
	clienteManager: string;
	gerenteProyecto: string;
	poseePlan: boolean;
	detailKeysResourcesList: DetPlanCont[];
}	