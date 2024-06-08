import { ProjectAlarm } from './ProjectAlarm';
import { Range } from './Range';
import { Funding } from './Funding';
import { Survay } from './Survay';
import { ProjectProfile } from './ProjectProfile';
import { Modification } from './Modification';
import { StaffFields } from './StaffFields';
import { ServiceFields } from './ServiceFields';
import { TraditionalFields } from './TraditionalFields';
import { TechnicalFields } from './TechnicalFields';
import { ProjectHistory } from './ProjectHistory';

export class ProjectTracking {
    // General
    sector: string;
    un: string;
    ce: string;
    client: string;
    serviceLine: string;
    projectManager: string;
    projectName: string;
    projectCode: string;
    projectType: string;
    projectTypeCode: number;
    startDate: Date;
    endDate: Date;
    modification: Modification;
    realEndDate: Date;
    alarm: ProjectAlarm;
    fundingReport:Funding;
    survayReport:Survay;
    
    
    attempts: number;

    storiesInBacklog: number;
    closedStories: number;
    percentageCompletion: number;
    remainingSprints: number;
    cycleTime: number;
    
    //Staff
    staff:StaffFields;

    //Agile Project
    plannedWork: number;
    percentageProd: number;
    itemsTechnicalDebt: number;
    percentageDebt: number;
    incidence: number;
    numberCells: number;
    sprintDuration: number;
    
    // Traditional Project 
    traditional:TraditionalFields;
    
    // Service   
    service:ServiceFields;

    //technical
    technical:TechnicalFields;

    // Agile
    numFinishedSprintStories: number;
    numReleases: number;
    numSprints: number;
    teamSpeedAverage: number;
    numSprintPlannedHistories: number;
    sprintComplInd: string;

    //extension contra proyecto
    declaredExtension: boolean = false;
    extAgainstIncome: boolean = false;
    extensionReason: string;
    extensionValidated: boolean = false;

    //range
    redMargin: Range;
    yellowMargin: Range;
    
   constructor(){
        this.fundingReport=new Funding("");
        this.survayReport=new Survay("");
        this.redMargin=new Range();
        this.yellowMargin=new Range();
        this.staff=new StaffFields();
        this.service=new ServiceFields();
        this.traditional=new TraditionalFields();
        this.technical=new TechnicalFields();
    }
}
