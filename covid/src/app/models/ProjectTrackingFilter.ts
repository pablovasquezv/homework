export class ProjectTrackingFilter {
    sector: string;
    un: string;
    ce: string;
    client: string;
    serviceLine: string;
    projectManager: string;
    projectCode: string;
    projectType: string;
    startDate: Date;
    endDate: Date;
    projectName: string;

    constructor(
        sector: string,
        un: string,
        ce: string,
        client: string,
        serviceLine: string,
        projectManager: string,
        projectCode: string,
        projectName: string,
        projectType: string,
        startDate: Date,
        endDate: Date,
    ) {
        this.sector = sector;
        this.un = un;
        this.ce = ce;
        this.client = client;
        this.serviceLine = serviceLine;
        this.projectManager = projectManager;
        this.projectCode = projectCode;
        this.projectType = projectType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectName=projectName;
    }
}