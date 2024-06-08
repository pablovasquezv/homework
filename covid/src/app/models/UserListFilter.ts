export class UserListFilter {
    unidad: string;
    cliente: string;
    sector: string;
    proyect: string;
    donde: string;
    cuarentena: string;
    cuarentenaFlag: boolean;
    covid19: string;
    covid19Flag: boolean;

    constructor(
        unidad: string,
        cliente: string,
        sector: string,
        proyect: string,
        donde: string,
        cuarentena: string,
        cuarentenaFlag: boolean,
        covid19: string,
        covid19Flag: boolean
    ) {
        this.unidad = unidad;
        this.cliente = cliente;
        this.sector = sector;
        this.proyect = proyect;
        this.donde = donde;
        this.cuarentena = cuarentena;
        this.cuarentenaFlag = cuarentenaFlag;
        this.covid19 = covid19;
        this.covid19Flag = covid19Flag;
    }
}