export class ProjectProfile {
    //Project Profile
    profileID: number;
    profileName: string;
    tarifa: number;
    projectCode: string;

    constructor(projectCode: string="") {
        this.projectCode=projectCode;
	}
}