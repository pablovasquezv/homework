export class Funding{
    projectCode: string;
    projectName: string;

    diasWIP: number;
    facturado: number;
    funding: number;
    incurredVsFunding: number;
    nrIncurrido: number;
    nrn: number;
    wip: number;
    wipconNRN: number;

    constructor(projectCode:string){this.projectCode=projectCode}
}