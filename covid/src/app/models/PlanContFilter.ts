export class PlanContFilter {
    projectCode: string;
    nombreProyecto: string;
	un: string;
	sector: string;
	ce: string;
	nombreCliente: string;
	clienteManager: string;
	gerenteProyecto: string;

    constructor(
        projectCode: string,
        nombreProyecto: string,
        un: string,
        sector: string,
        ce: string,
        nombreCliente: string,
        clienteManager: string,
        gerenteProyecto: string
    ) {
        this.projectCode = projectCode;
        this.nombreProyecto = nombreProyecto;
        this.un = un;
        this.sector = sector;
        this.ce = ce;
        this.nombreCliente = nombreCliente;
        this.clienteManager = clienteManager;
        this.gerenteProyecto = gerenteProyecto;
    }
}