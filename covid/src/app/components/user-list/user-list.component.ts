import {Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import Swal from 'sweetalert2';

import { ExcelService } from './excel.service';
import { UserListFilterService } from './user-list-filter.service';
import { DataService } from './../../services/data.service';
import { HomeworkService } from './../../services/homework.service';
import { Homework } from './../../models/Homework';
import { UserListFilter } from './../../models/UserListFilter';
import { formatDate } from '@angular/common';


@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit {

  users: Array<Homework>;
  usersFilter: Array<Homework>;
  usersExport: Array<Homework>;

  displayedColumns: string[] = ['unidad', 'cliente', 'nombre', 'usuario','empleado', 'donde', 'cuarentena', 'comienzo', 'covid', 'diagnostico', 'opt'];
  dataSource: MatTableDataSource<Homework>;

  // Filtros
  filterUserList: UserListFilter;
  unidadList: string[] = [];
  clienteList: string[] = [];
  sectorList: string[] = [];
  proyectoList: string[] = [];
  dondeList: string[] = [];

  filtroUnidad: string = "";
  filtroCliente: string = "";
  filtroSector: string = "";
  filtroProyecto: string = "";
  filtroDonde: string = "";
  filtroCuarentena: boolean = false;
  filtroCOVID19: boolean = false;
  filtroCuarentenaStr = '';
  filtroCOVID19Str = '';
  
  public paginatorSize:number=10;

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild("fileUpload", {static: false}) fileUpload: ElementRef;files  = [];
  public currentUser: string="";

  constructor(private _dataService: DataService,
    private _homeworkService: HomeworkService,
    private excelService: ExcelService,
    private userListFilterService: UserListFilterService) { }

  ngOnInit() {
    this.getAllHomework();
    this.currentUser=localStorage.getItem("currentUser");

    if(localStorage.getItem("userlist_pageSize")!=null) 
      this.paginatorSize = parseInt(localStorage.getItem("userlist_pageSize"));
  }

  handlePaginatorEvent(event : any){
    this.paginatorSize=event.pageSize;
    localStorage.setItem("userlist_pageSize",this.paginatorSize+"");
  }

  getAllHomework(){
    this._dataService.getAll().subscribe(
      (res)=>{
         this.users = res;
         this.usersExport = this.users;
         this.dataSource = new MatTableDataSource(this.users);
         this.dataSource.paginator = this.paginator;
         this.paginator._intl.itemsPerPageLabel = 'Registros por página.'
         this.dataSource.sort = this.sort;
         this.loadFilters();
      },
      (err)=>{
        console.log(err);
      });
  }

  loadFilters(){
    this.unidadList = [];
    this.clienteList = [];
    this.sectorList = [];
    this.proyectoList = [];
    this.dondeList = [];
    this.users.forEach(homeworkItem => {
      // Unidad
      if(homeworkItem.unidad != '' && this.unidadList.indexOf(homeworkItem.unidad) === -1) {
        this.unidadList.push(homeworkItem.unidad);
      }
      // Cliente
      if(homeworkItem.cliente != '' && this.clienteList.indexOf(homeworkItem.cliente) === -1) {
        this.clienteList.push(homeworkItem.cliente);
      }
      // Sector
      if(homeworkItem.sector != '' && this.sectorList.indexOf(homeworkItem.sector) === -1) {
        this.sectorList.push(homeworkItem.sector);
      }
      // Proyecto
      if(homeworkItem.proyect != '' && this.proyectoList.indexOf(homeworkItem.proyect) === -1) {
        this.proyectoList.push(homeworkItem.proyect);
      }
      // Donde
      if(homeworkItem.donde != '' && this.dondeList.indexOf(homeworkItem.donde) === -1) {
        this.dondeList.push(homeworkItem.donde);
      }
    });
    // Orden alfabetico
    this.unidadList.sort((a, b) => a.localeCompare(b));
    this.clienteList.sort((a, b) => a.localeCompare(b));
    this.sectorList.sort((a, b) => a.localeCompare(b));
    this.proyectoList.sort((a, b) => a.localeCompare(b));
    this.dondeList.sort((a, b) => a.localeCompare(b));

    this.chargeFilters();
  }

  chargeFilters(){
    this.filterUserList = this.userListFilterService.getFilters();
    if(this.filterUserList !== undefined){
      this.filtroUnidad = this.filterUserList.unidad;
      this.filtroCliente = this.filterUserList.cliente;
      this.filtroSector = this.filterUserList.sector;
      this.filtroProyecto = this.filterUserList.proyect;
      this.filtroDonde = this.filterUserList.donde;
      this.filtroCuarentena = this.filterUserList.cuarentenaFlag;
      this.filtroCOVID19 = this.filterUserList.covid19Flag;
      this.filtroCuarentenaStr = this.filterUserList.cuarentena;
      this.filtroCOVID19Str = this.filterUserList.covid19;
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
    this.usersFilter = Array<Homework>();
    let eqUnidad: boolean = false;
    let eqCliente: boolean = false;
    let eqSector: boolean = false;
    let eqProyecto: boolean = false;
    let eqDonde: boolean = false;
    let eqCuarentena: boolean = false;
    let eqCOVID19: boolean = false;

    // Si no existen filtros ingresados no se realizara busqueda
    if(this.filtroUnidad == "" &&
        this.filtroCliente == "" &&
        this.filtroSector == "" &&
        this.filtroProyecto == "" &&
        this.filtroDonde == "" &&
        this.filtroCuarentenaStr == "" &&
        this.filtroCOVID19Str == ""){
          return;
    }

    this.filterUserList = new UserListFilter(
      this.filtroUnidad,
      this.filtroCliente,
      this.filtroSector,
      this.filtroProyecto,
      this.filtroDonde,
      this.filtroCuarentenaStr,
      this.filtroCuarentena,
      this.filtroCOVID19Str,
      this.filtroCOVID19
    );
    this.userListFilterService.setFilters(this.filterUserList);

    // Filtro por unidad
    this.users.forEach(homeworkItem => {
      eqUnidad = false;
      eqCliente = false;
      eqSector = false;
      eqProyecto = false;
      eqDonde = false;
      eqCuarentena = false;
      eqCOVID19 = false;
      // Filtro unidad
      if(this.filtroUnidad == "" || this.filtroUnidad === homeworkItem.unidad){
        eqUnidad = true;
      }

      // Filtro cliente
      if(this.filtroCliente == "" || 
        (this.filtroCliente === 'NA' && homeworkItem.cliente == '') ||
        this.filtroCliente === homeworkItem.cliente){
        eqCliente = true;
      }

      // Filtro sector
      if(this.filtroSector == "" || 
        (this.filtroSector === 'NA' && homeworkItem.sector == '') ||
        this.filtroSector === homeworkItem.sector){
        eqSector = true;
      }

      // Filtro proyecto
      if(this.filtroProyecto == "" || 
        (this.filtroProyecto === 'NA' && homeworkItem.proyect == '') ||
        this.filtroProyecto === homeworkItem.proyect){
        eqProyecto = true;
      }

      // Filtro donde
      if(this.filtroDonde == "" || this.filtroDonde === homeworkItem.donde){
        eqDonde = true;
      }

      // Filtro cuarentena
      if(this.filtroCuarentenaStr == "" || this.filtroCuarentena === homeworkItem.cuarentena){
        eqCuarentena = true;
      }

      // Filtro COVID19
      if(this.filtroCOVID19Str == "" || this.filtroCOVID19 === homeworkItem.covid19){
        eqCOVID19 = true;
      }

      // Check flags
      if(eqUnidad && eqCliente && eqSector && eqProyecto && eqDonde && eqCuarentena && eqCOVID19){
        this.usersFilter.push(homeworkItem);
      }
    });
   
    // Recarga de lista
    this.dataSource = new MatTableDataSource(this.usersFilter);
    this.dataSource.paginator = this.paginator;
    this.paginator._intl.itemsPerPageLabel = 'Registros por página.'
    this.dataSource.sort = this.sort;
    this.usersExport = this.usersFilter;
  }

  limpiar() {
    this.filterUserList = new UserListFilter('', '', '', '', '', '', false, '', false);
    this.userListFilterService.setFilters(this.filterUserList);
    this.filtroUnidad = this.filterUserList.unidad;
    this.filtroCliente = this.filterUserList.cliente;
    this.filtroSector = this.filterUserList.sector;
    this.filtroProyecto = this.filterUserList.proyect;
    this.filtroDonde = this.filterUserList.donde;
    this.filtroCuarentena = this.filterUserList.cuarentenaFlag;
    this.filtroCOVID19 = this.filterUserList.covid19Flag;
    this.filtroCuarentenaStr = this.filterUserList.cuarentena;
    this.filtroCOVID19Str = this.filterUserList.covid19;
    // Recarga de lista
    this.dataSource = new MatTableDataSource(this.users);
    this.dataSource.paginator = this.paginator;
    this.paginator._intl.itemsPerPageLabel = 'Registros por página.'
    this.dataSource.sort = this.sort;
    this.usersExport = this.users;

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  changeUnidad(event){
    this.filtroUnidad = event.value;
  }

  changeCliente(event){
    this.filtroCliente = event.value;
  }

  changeSector(event){
    this.filtroSector = event.value;
  }

  changeProyecto(event){
    this.filtroProyecto = event.value;
  }

  changeDonde(event){
    this.filtroDonde = event.value;
  }

  changeCuarentena(event){
    if(event.value == "1"){
      this.filtroCuarentena = true;
    }else{
      this.filtroCuarentena = false;
    }
  }

  changeCOVID19(event){
    if(event.value == "1"){
      this.filtroCOVID19 = true;
    }else{
      this.filtroCOVID19 = false;
    }
  }

  deleteHomeWork(row){
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
        this._homeworkService.deleteById(row.usuario).subscribe(
          (response) => {
            this.getAllHomework();
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
    this._homeworkService.upload(formData).subscribe(
    (rr) => {
      Swal.fire({
        icon: 'success',
        title: '¡Archivo cargado!',
        showConfirmButton: true
      })
      this.getAllHomework();
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
    // Se insertan filas vacias
    dataExport.push(this.createCellEmpty());
    // Se inserta celda con titulos
    dataExport.push(this.createCellHeader());
    this.usersExport.forEach(homeworkItem => {
      dataExport.push(this.createCellExport(homeworkItem));
    });
    this.excelService.exportAsExcelFile(dataExport, 'result');
  }

  createCellHeader(){
    return {
      'Unidad ITS&S': 'Cliente',
      '  ': 'Código colaborador',
      '   ': 'Usuario',
      '    ': 'Nombre',
      '     ': 'Apellidos',
      '      ': 'Donde',
      '       ': 'Sector',
      '        ': 'Project',
      '         ': 'Project2',
      '          ': 'Comentarios',
      '           ': 'UN',
      '            ': 'En cuarentena por exposición',
      '             ': 'Fecha de comienzo de cuarentena',
      '              ': 'Coronavirus positivo',
      '               ': 'Fecha Diagnóstico',
      '                ': 'Teléfono de contacto (opcional)',
      '                 ': 'Dirección',
      '                  ': 'Comuna',
      '                   ': 'Región',
      '                    ': 'Oficina',
      '                     ': 'Fecha de inicio',
      '                      ': 'Fecha de fin'
    }; 
  }
  
  createCellEmpty(){
    return {
      'Unidad ITS&S': '',
      '  ': '',
      '   ': '',
      '    ': '',
      '     ': '',
      '      ': 'Remoto desde casa / Everis / Cliente / Vacaciones / Licencia / No aplica',
      '       ': '',
      '        ': '',
      '         ': '',
      '          ': '',
      '           ': 'Bpo, Business, Centers, ECS, Exceleria, Initiatives, Initiatives Companies, ITS&S, Oversis, Ses, Solutions, Structure, Technology, Training',
      '            ': 'Si o No',
      '             ': 'dd/mm/aaaa',
      '              ': 'Si o No',
      '               ': 'dd/mm/aaaa',
      '                ': '',
      '                  ': 'Aplica para cuando campo Donde posea los valores Remoto desde casa o Cliente',
      '                   ': 'Aplica para cuando campo Donde posea los valores Remoto desde casa o Cliente',
      '                    ': 'Aplica para cuando campo Donde posea los valores Remoto desde casa o Cliente',
      '                     ': 'Posibles valores [Alameda, Coe, Temuco]. Aplica para cuando campo Donde posea el valor Everis',
      '                       ': 'dd/mm/aaaa. Aplica para cuando campo Donde posea los valores Licencia o Vacaciones',
      '                        ': 'dd/mm/aaaa. Aplica para cuando campo Donde posea los valores Licencia o Vacaciones'
    }; 
  }

  createCellExport(homeworkItem : Homework){
    return {
      'Unidad ITS&S': homeworkItem.cliente,
      '  ': homeworkItem.nroEmpleado,
      '   ': homeworkItem.userName,
      '    ': homeworkItem.nombre,
      '      ': homeworkItem.apellido,
      '       ': homeworkItem.donde,
      '         ': homeworkItem.sector,
      '          ': homeworkItem.proyect,
      '            ': homeworkItem.proyect2,
      '             ': homeworkItem.comentario,
      '               ': homeworkItem.unidad,
      '                ': homeworkItem.cuarentena ? 'Si' : 'No',
      '                 ': homeworkItem.inicioCuarentena ? formatDate(homeworkItem.inicioCuarentena, 'dd/MM/yyyy', 'en_US'): '',
      '                   ': homeworkItem.covid19 ? 'Si' : 'No',
      '                     ': homeworkItem.fechaDiagnostico ? formatDate(homeworkItem.fechaDiagnostico, 'dd/MM/yyyy', 'en_US') : '',
      '                      ': homeworkItem.telefono,
      '                       ': homeworkItem.direccion,
      '                         ': homeworkItem.comuna,
      '                          ': homeworkItem.region,
      '                           ': homeworkItem.oficinaEveris,
      '                             ': homeworkItem.fechaInicio ? formatDate(homeworkItem.fechaInicio, 'dd/MM/yyyy', 'en_US') : '',
      '                               ': homeworkItem.fechaFin ? formatDate(homeworkItem.fechaFin, 'dd/MM/yyyy', 'en_US') : ''
    };
  }
}
