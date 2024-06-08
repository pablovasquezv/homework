export class Doubt {
    doubt: string;
    projectCode: string;
    from: string
    constructor(doubt: string, projectCode: string, from: string) {
        this.doubt = doubt;
        this.projectCode = projectCode;
        this.from = from;
    }

}