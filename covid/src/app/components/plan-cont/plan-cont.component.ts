import {Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import Swal from 'sweetalert2';
import { PlanContFilterService } from './plan-cont-filter.service';
import { DataService } from '../../services/data.service';
import { PlanContService } from 'src/app/services/plancont.service';
import { PlanCont } from 'src/app/models/PlanCont';
import { PlanContFilter } from 'src/app/models/PlanContFilter';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DetPlanCont } from 'src/app/models/DetPlanCont';
import { ExcelExportService } from 'src/app/services/excelExport.service';
import { ContDocument } from 'src/app/models/ContDocument';

@Component({
  selector: 'app-plan-cont',
  templateUrl: './plan-cont.component.html',
  styleUrls: ['./plan-cont.component.scss']
})
export class PlanContComponent implements OnInit {

  fileUrl;

  public base64textString:String="";
  public base64:any;


  planCont: Array<PlanCont>;
  planContFilter: Array<PlanCont>;
  planContExport: Array<PlanCont>;
  documentoDecodificado: String;
  totalResults: number;

 /* arreglo de columnas que incluye columna con icono de documento para los proyecos adjuntos */ 
  /* displayedColumns: string[] = ['UN','sector','CE','nombreCliente','projectCode', 'nombreProyecto','clienteManager','gerenteProyecto','documPlanContingencia','opt']; */
  displayedColumns: string[] = ['UN','sector','CE','nombreCliente','projectCode', 'nombreProyecto','clienteManager','gerenteProyecto','poseePlan','opt'];
  dataSource: MatTableDataSource<PlanCont>;

  // Filtros
  filterPlanContList: PlanContFilter;
  projectCodeList: string[] = [];
  projectNameList: string[] = [];
  unList: string[] = [];
  sectorList: string[] = [];
  ceList: string[] = [];
  nombreClienteList: string[] = [];
  clienteManagerList: string[] = [];
  gerenteProyectoList: string[] = [];

  filtroProjectCode: string = "";
  filtroProjectName: string = "";
  filtroUnidad: string = "";
  filtroSector: string = "";
  filtroCE: string = "";
  filtroNombreCliente: string = "";
  filtroClienteManager: string = "";
  filtroGerenteProyecto: string = "";


  
  public paginatorSize:number=10;

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild("fileUpload", {static: false}) fileUpload: ElementRef;files  = [];

  constructor(private _dataService: DataService,
    private _planContService: PlanContService,
    private excelPlanContService: ExcelExportService,
    private planContFilterService: PlanContFilterService,
    private modalService: NgbModal
    ) { }

  ngOnInit() {
    this.getAllPlanCont();
    if(localStorage.getItem("plancontlist_pageSize")!=null) 
      this.paginatorSize = parseInt(localStorage.getItem("plancontlist_pageSize"));
  }

  handlePaginatorEvent(event : any){
    this.paginatorSize=event.pageSize;
    localStorage.setItem("plancontlist_pageSize",this.paginatorSize+"");
  }


  loadPaginator(data: Array<PlanCont>) {
    this.dataSource = new MatTableDataSource(data);
    this.dataSource.paginator = this.paginator;
    this.paginator._intl.itemsPerPageLabel = 'Registros por página.'
    this.dataSource.sortingDataAccessor = (item, property) => {
      switch(property) {
        case 'UN': return item.un;
        default: return item[property];
      }
    };
    this.dataSource.sort = this.sort;
  }

  getAllPlanCont(){
    
    this._planContService.getAllPlanCont().subscribe(
      (res)=>{
        this.planCont = res;
        console.log("get all");
        this.planContExport = this.planCont;
        this.loadPaginator(this.planCont);
        this.loadFilters();
      },
      (err)=>{
        console.log(err);
      });
  }

  loadFilters(){
    this.projectCodeList = [];
    this.projectNameList = [];
    this.unList = [];
    this.sectorList = [];
    this.ceList = [];
    this.nombreClienteList = [];
    this.clienteManagerList = [];
    this.gerenteProyectoList = [];
    this.planCont.forEach(planContItem => {

      // codigo proyecto
      if(planContItem.projectCode != '' && this.projectCodeList.indexOf(planContItem.projectCode) === -1) {
        this.projectCodeList.push(planContItem.projectCode);
      }

      // nombre proyecto
      if(planContItem.nombreProyecto != '' && this.projectNameList.indexOf(planContItem.nombreProyecto) === -1) {
        this.projectNameList.push(planContItem.nombreProyecto);
      }

      // un
      if(planContItem.un != '' && this.unList.indexOf(planContItem.un) === -1) {
        this.unList.push(planContItem.un);
      }

      // sector
      if(planContItem.sector != '' && this.sectorList.indexOf(planContItem.sector) === -1) {
        this.sectorList.push(planContItem.sector);
      }

      // ce
      if(planContItem.ce != '' && this.ceList.indexOf(planContItem.ce) === -1) {
        this.ceList.push(planContItem.ce);
      }

      // nombre cliente
      if(planContItem.nombreCliente != '' && this.nombreClienteList.indexOf(planContItem.nombreCliente) === -1) {
        this.nombreClienteList.push(planContItem.nombreCliente);
      }

      // gerente proyecto
      if(planContItem.gerenteProyecto != '' && this.gerenteProyectoList.indexOf(planContItem.gerenteProyecto) === -1) {
        this.gerenteProyectoList.push(planContItem.gerenteProyecto);
      }

      // cliente manager
      if(planContItem.clienteManager != '' && this.clienteManagerList.indexOf(planContItem.clienteManager) === -1) {
        this.clienteManagerList.push(planContItem.clienteManager);
      }

    });

    
    this.projectCodeList.sort((a, b) => a.localeCompare(b));
    this.projectNameList.sort((a, b) => a.localeCompare(b));
    this.unList.sort((a, b) => a.localeCompare(b));
    this.sectorList.sort((a, b) => a.localeCompare(b));
    this.ceList.sort((a, b) => a.localeCompare(b));
    this.nombreClienteList.sort((a, b) => a.localeCompare(b));
    this.clienteManagerList.sort((a, b) => a.localeCompare(b));
    this.gerenteProyectoList.sort((a, b) => a.localeCompare(b));
 
    this.chargeFilters(); 
  }

  chargeFilters(){
    this.filterPlanContList = this.planContFilterService.getFilters();
    
    if(this.filterPlanContList !== undefined){

      this.filtroProjectCode=this.filterPlanContList.projectCode;
      this.filtroProjectName=this.filterPlanContList.nombreProyecto;
      this.filtroUnidad=this.filterPlanContList.un;
      this.filtroSector=this.filterPlanContList.sector;
      this.filtroCE=this.filterPlanContList.ce;
      this.filtroNombreCliente=this.filterPlanContList.nombreCliente;
      this.filtroClienteManager=this.filterPlanContList.clienteManager;
      this.filtroGerenteProyecto=this.filterPlanContList.gerenteProyecto;

    
     
      this.searchByFilters();
    } 
  }

  applyFilter(event: Event) {
   const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    } 
  }

  searchByFilters(){
    console.log("searchByFilters");
    this.planContFilter = Array<PlanCont>();
    let eqProjectCode: boolean = false;
    let eqProjectName: boolean = false;
    let eqUnidad: boolean = false;
    let eqSector: boolean = false;
    let eqCE: boolean = false;
    let eqNombreCliente: boolean = false;
    let eqClienteManager: boolean = false;
    let eqGerenteProyecto: boolean = false;

    // Si no existen filtros ingresados no se realizara busqueda
    if(this.filtroProjectCode == "" &&
    this.filtroProjectName == "" &&
    this.filtroUnidad == "" &&
    this.filtroSector == "" &&
    this.filtroCE == "" &&
    this.filtroNombreCliente == "" &&
    this.filtroGerenteProyecto  == "" &&
    this.filtroClienteManager == ""){
      return;
    }

    this.filterPlanContList = new PlanContFilter(
      this.filtroProjectCode,
      this.filtroProjectName,
      this.filtroUnidad,
      this.filtroSector,
      this.filtroCE,
      this.filtroNombreCliente,
      this.filtroGerenteProyecto,
      this.filtroClienteManager);

      this.planContFilterService.setFilters(this.filterPlanContList);

      this.planCont.forEach(planContItem => {
        eqProjectCode = false;
        eqProjectName = false;
        eqUnidad = false;
        eqSector = false;
        eqCE = false;
        eqNombreCliente = false;
        eqGerenteProyecto = false;
        eqClienteManager = false;
        
        // Filtro cod proyecto
        if(this.filtroProjectCode == "" || this.filtroProjectCode === planContItem.projectCode){
          eqProjectCode = true;
        }

        // Filtro nombre proyecto
        if(this.filtroProjectName == "" || this.filtroProjectName === planContItem.nombreProyecto){
          eqProjectName = true;
        }

        // Filtro un
        if(this.filtroUnidad == "" || this.filtroUnidad === planContItem.un){
          eqUnidad = true;
        }

        // Filtro sector
        if(this.filtroSector == "" || this.filtroSector === planContItem.sector){
          eqSector = true;
        }

        // Filtro ce
        if(this.filtroCE == "" || this.filtroCE === planContItem.ce){
          eqCE = true;
        }

        // Filtro nombre cliente
        if(this.filtroNombreCliente == "" || this.filtroNombreCliente === planContItem.nombreCliente){
          eqNombreCliente = true;
        }

        // Filtro nombre cliente
        if(this.filtroGerenteProyecto == "" || this.filtroGerenteProyecto === planContItem.gerenteProyecto){
          eqGerenteProyecto = true;
        }

        // Filtro nombre cliente
        if(this.filtroClienteManager == "" || this.filtroClienteManager === planContItem.clienteManager){
          eqClienteManager = true;
        }


        // Check flags
      if(eqProjectCode && eqProjectName && eqUnidad && eqSector && eqCE && eqNombreCliente && eqGerenteProyecto && eqClienteManager){
        this.planContFilter.push(planContItem);
      }

      });
     
    
   
    // Recarga de lista
    this.dataSource = new MatTableDataSource(this.planContFilter);
    this.dataSource.paginator = this.paginator;
    this.paginator._intl.itemsPerPageLabel = 'Registros por página.';
    this.dataSource.sort = this.sort;
    this.planContExport = this.planContFilter;
    this.totalResults = this.totalRows;
  }

  get totalRows(): number {
    return this.planContFilter.length;
  }

  limpiar() {
    this.filterPlanContList = new PlanContFilter('', '', '', '', '', '', '', '');
    this.planContFilterService.setFilters(this.filterPlanContList);

    this.filtroProjectCode = this.filterPlanContList.projectCode;
    this.filtroProjectName = this.filterPlanContList.nombreProyecto;
    this.filtroUnidad = this.filterPlanContList.un;
    this.filtroSector = this.filterPlanContList.sector;
    this.filtroCE = this.filterPlanContList.ce;
    this.filtroNombreCliente = this.filterPlanContList.nombreCliente;
    this.filtroClienteManager = this.filterPlanContList.clienteManager;
    this.filtroGerenteProyecto = this.filterPlanContList.gerenteProyecto;
    this.totalResults=null;
    

    // Recarga de lista
    this.dataSource = new MatTableDataSource(this.planCont);
    this.dataSource.paginator = this.paginator;
    this.paginator._intl.itemsPerPageLabel = 'Registros por página.'
    this.dataSource.sort = this.sort;
    this.planContExport = this.planCont;

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    } 
    
  }

  deletePlanCont(row){
     Swal.fire({
      title: '¿Está seguro de eliminar el registro?',
      text: "¡No podrá revertir este cambio!",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, eliminar registro!'
    }).then((result) => {
      if (result.value) {
        this._planContService.deleteById(row.projectCode).subscribe(
          (response) => {
            this.getAllPlanCont();
            Swal.fire(
              '¡Registro borrado!',
              'El registro ha sido eliminado.',
              'success'
            )
          }, (error) => {
            console.log(error);
          }
        );
      }
    }) 
  }

  isValidProfile(valor: String){
  
    if(valor === null){
      return false;
    }
    return true;
    
    
  }

  verDocumento(revent,modaldoc){
    console.log('DOCUMENTO: ' + revent);
    this.documentoDecodificado = undefined;
    
    const file = new Blob([this.dataURItoBlob(revent)]);
    const url = window.URL.createObjectURL(file);

    this.documentoDecodificado =  url;
    console.log('visualizando documento');
   
    this.modalService.open(modaldoc, { centered: true, windowClass: 'modalDocum' });
  }

  dataURItoBlob(dataURI) {
    const binary = atob(dataURI.split(',')[1]);
    const array = [];
    for (let i = 0; i < binary.length; i++) {
      array.push(binary.charCodeAt(i));
    }
    return new Blob([new Uint8Array(array)]);
  }

  downloadDocument(plan: PlanCont) {
    console.log('buscando documento');
    this._planContService.getDocument(plan.projectCode).subscribe(
      (res) => {
        let doc = new ContDocument();
        doc = res;

        let url = doc.contentBase64;
        fetch(url)
          .then(res => res.blob())
          .then(function (blob) {
            if (window.navigator.msSaveOrOpenBlob) {
              window.navigator.msSaveBlob(blob, doc.name);
            } else {
              var elem = window.document.createElement('a');
              elem.href = window.URL.createObjectURL(blob);
              elem.download = doc.name;
              document.body.appendChild(elem);
              elem.click();
              document.body.removeChild(elem);
            }
          });
      }
    );
  }

  

  upload() {
       const fileUpload = this.fileUpload.nativeElement;
      fileUpload.onchange = () => {
      this.files=[];
      for (let index = 0; index < fileUpload.files.length; index++) {
         const file = fileUpload.files[index];
         this.files.push({ data: file, inProgress: false, progress: 0});
      }

      this.uploadFiles();
      };

      fileUpload.click(); 
  }

  private uploadFiles() {
       this.fileUpload.nativeElement.value = '';
      this.files.forEach(file => {
        this.uploadFile(file);
      }); 
  }

  uploadFile(file) {  
     const formData = new FormData();
    formData.append('file', file.data);

    // Se abre PopUp
    Swal.fire({
          title: 'Cargando archivo',
          text: 'Esto puede tardar tiempo',
          allowEscapeKey: false,
          allowOutsideClick: false,
          showConfirmButton: false,
          onOpen: ()=>{
              Swal.showLoading();
          }
      });
    
    file.inProgress = true;
    this._planContService.upload(formData).subscribe(
    (rr) => {
      Swal.fire({
        icon: 'success',
        title: '¡Archivo cargado!',
        showConfirmButton: true
      })
      this.getAllPlanCont();
    }, (err) => {
      Swal.fire({
        icon: 'error',
        title: 'Ha ocurrido un error',
        text: 'Verifique la información contenida en el archivo'
      })
      console.log(err);
    });
  }

  exportAsXLSX() {
     let dataExport : any[] = [];
    this.planContExport.forEach(planItem => {
      planItem.detailKeysResourcesList.forEach(planResource => {
        dataExport.push(this.createCellExport(planItem,planResource));
      });
    });
    this.excelPlanContService.exportAsExcelFile(dataExport, 'PlanContingencia'); 
  }

  formatBoolean(value: Boolean): string {
    if (value) return "Si";
    else return "No";
  }

  toYNNOption(option:number):string{
    if(option==0)return "NO";
    else if(option==1) return "SI";
    else return "N/A";
  }
  createCellExport(ptItem: PlanCont, planResource: DetPlanCont) {
    let row = {
      // General
      'UN': ptItem.un,
      'Sector': ptItem.sector,
      'CE': ptItem.ce,
      'Nombre Cliente': ptItem.nombreCliente,
      'Código Proyecto': ptItem.projectCode,
      'Nombre de Proyecto': ptItem.nombreProyecto,
      'Client Manager': ptItem.clienteManager,
      'Gerente de Proyecto': ptItem.gerenteProyecto,
      //Detalles del plan
      'Perfil Clave': planResource.perfilClave,
      'Nombre Perfil Clave': planResource.nombrePerfilClave,
      'Nombre Backup': planResource.nombreBackup,
      'Tiene plan de contingencia / continuidad que contenga esta información?':this.toYNNOption(planResource.planContingencia),
      'El backup conoce las actividades que debe realizar ?': this.toYNNOption(planResource.backupActividades),
      'El backup conoce los repositorios, herramientas, accesos etc., que se utiliza en el proyectos?': this.toYNNOption(planResource.backupHerramientas),
      'Si el perfil clave es un líder, este ha participado en reunión de seguimiento?':this.toYNNOption(planResource.pclaveReunionSeg),
      'Se consideró la comunicación del plan al cliente y al equipo?':this.toYNNOption(planResource.comunicacionCliEquipo),
      'Comentarios':planResource.comentarios,
      'Plan de contingencia': ptItem.poseePlan ? "Posee plan" : "Sin plan",
    };
    return row;
  }
}
