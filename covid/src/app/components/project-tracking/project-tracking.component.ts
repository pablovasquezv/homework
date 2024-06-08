import { Component, OnInit, ViewChild, ElementRef, QueryList, ViewChildren, AfterViewInit } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import Swal from 'sweetalert2';

import { formatDate } from '@angular/common';
import { ProjectTracking } from './../../models/ProjectTracking';
import { ProjectTrackingFilter } from './../../models/ProjectTrackingFilter';
import { ProjectTrackingService } from './../../services/projectTracking.service';
import { ProjectTrackingFilterService } from './project-tracking-filter.service';
import { FindCompleteComponent } from '../../components/find-complete/find-complete.component';

import { Profile } from './../../models/Profile'
import { Rol } from './../../models/Rol';
import { ProfileService } from '../../services/profile.service';
import { Router } from '@angular/router';
import { DoubtsService } from '../../services/doubts.service';
import { Doubt } from '../../models/Doubt';
import { ExcelExportService } from 'src/app/services/excelExport.service';

@Component({
  selector: 'app-project-tracking',
  templateUrl: './project-tracking.component.html',
  styleUrls: ['./project-tracking.component.scss']
})
export class ProjectTrackingComponent implements OnInit {
  profile: Profile = null;

  projectTrackings: Array<ProjectTracking>;
  projectTrackingsFilter: Array<ProjectTracking>;
  projectTrackingsExport: Array<ProjectTracking>;

  displayedColumns: string[] = ['alarm', 'sector', 'un', 'ce', 'client', 'serviceLine', 'projectManager', 'projectCode', 'projectName', 'projectType', 'realEndDate', 'lastModified', 'opt'];
  dataSource: MatTableDataSource<ProjectTracking>;

  // Filtros
  filterProjectTracking: ProjectTrackingFilter = new ProjectTrackingFilter("", "", "", "", "", "", "", "", "", null, null);
  sectorList: string[] = [];
  unList: string[] = [];
  ceList: string[] = [];
  clientList: string[] = [];
  serviceLineList: string[] = [];
  projectManagerList: string[] = [];
  projectCodeList: string[] = [];
  projectTypeList: string[] = [];
  projectNameList: string[] = [];
  totalResults: number;

  public paginatorSize: number = 10;
  public domain: string = "SEGUIMIENTO_PROYECTOS";

  //TABLA
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild("fileUpload", { static: false }) fileUpload: ElementRef;

  constructor(private router: Router,
    private projectTrackingService: ProjectTrackingService,
    private excelProjectService: ExcelExportService,
    private projectTrackingFilterService: ProjectTrackingFilterService,
    private profileService: ProfileService,
    private doubtsService: DoubtsService) {
  }

  ngOnInit() {
    this.getAllProjectTracking();

    if (localStorage.getItem("tracking_pageSize") != null)
      this.paginatorSize = parseInt(localStorage.getItem("tracking_pageSize"));
    this.loadProfile();
  }

  mustEnableAccess(): boolean {
    if (this.profile == null || this.profile.roles == null) return false;

    for (var i = 0; i < this.profile.roles.length; i++) {
      if ((this.profile.roles[i].name.toUpperCase() === "EJECUTIVO") ||
        (this.profile.roles[i].name.toUpperCase() === "ADMINISTRADOR"))
        return true;
    }

    return false;
  }
  allowDelete(): boolean {
    if (this.profile == null || this.profile.roles == null) return false;

    for (var i = 0; i < this.profile.roles.length; i++) {
      if (this.profile.roles[i].name.toUpperCase() === "ADMINISTRADOR")
        return true;
    }
    return false;
  }

  isAdminProfile(): boolean {
    if (this.profile == null || this.profile.roles == null) return false;

    for (var i = 0; i < this.profile.roles.length; i++) {
      if (this.profile.roles[i].name.toLowerCase() === "administrador")
        return true;
    }
    return false;
  }

  loadProfile() {
    let user = localStorage.getItem("currentUser");
    if (user == null || user == '') return;
    this.profileService.getFilteredProfileFor(user, this.domain).subscribe(
      (res) => {
        this.profile = <Profile>(res);
        if (res != null) {
          Object.setPrototypeOf(this.profile, Profile.prototype);
          this.profile.setRolesNature();
        }
        else {
          this.profile = new Profile();
          this.profile.user = user;
          this.profile.roles = [];
        }
        //si no hay roles, cargo el 'DEFAULT'
        if (this.profile.roles.length == 0) {
          let rol = new Rol();
          rol.domain = this.domain;
          rol.name = "Default";
          this.profile.roles = [rol];
        }
        console.log(res);

        if (this.mustEnableAccess() == false) {
          this.router.navigate(['app']);
        }

      },
      (err) => {
        console.log(err);
        this.router.navigate(['//']);
      });
  }

  loadPaginator(data: Array<ProjectTracking>) {
    this.dataSource = new MatTableDataSource(data);
    this.dataSource.paginator = this.paginator;
    this.paginator._intl.itemsPerPageLabel = 'Registros por página.'
    this.dataSource.sortingDataAccessor = (item, property) => {
      switch (property) {
        case 'alarm': return item.alarm.level;
        case 'lastModified': return item.modification!=null?item.modification.lastModified:0;
        default: return item[property];
      }
    };
    this.dataSource.sort = this.sort;
  }

  getAllProjectTracking() {
    this.projectTrackingService.getAllProjectTracking().subscribe(
      (res) => {
        res.sort((a, b) => a.alarm.level.toString().localeCompare(b.alarm.level.toString()));
        this.projectTrackings = res;
        this.projectTrackingsExport = this.projectTrackings;
        this.loadPaginator(this.projectTrackings);
        this.loadFilters();
        this.projectTrackingsFilter = Array<ProjectTracking>();
      },
      (err) => {
        console.log(err);
      });
  }

  highlightRow(row: ProjectTracking): string {
    if (row.declaredExtension == false) return "";
    if (row.extAgainstIncome == null) { return "highlight-red"; }
    if (row.extensionValidated == true) return "";
    if (row.extAgainstIncome == true) return "highlight-yellow";

    return "";
  }

  getAlarmClass(level: number): string {
    if (level == null) return "";

    if (level == 1) return "material-icons color_red";
    if (level == 2) return "material-icons color_yellow";
    if (level == 3) return "material-icons color_green";
    if (level == 4) return "material-icons color_gray";

    return "";
  }

  handlePaginatorEvent(event: any) {
    this.paginatorSize = event.pageSize;
    localStorage.setItem("tracking_pageSize", this.paginatorSize + "");
  }

  loadFilters() {
    this.sectorList = [];
    this.unList = [];
    this.ceList = [];
    this.clientList = [];
    this.serviceLineList = [];
    this.projectManagerList = [];
    this.projectCodeList = [];
    this.projectNameList = [];
    this.projectTypeList = [];

    this.projectTrackings.forEach(ptItem => {
      // Sector
      if (ptItem.sector!=null && ptItem.sector != '' && this.sectorList.indexOf(ptItem.sector) === -1) {
        this.sectorList.push(ptItem.sector);
      }
      // UN
      if (ptItem.un!=null && ptItem.un != '' && this.unList.indexOf(ptItem.un) === -1) {
        this.unList.push(ptItem.un);
      }
      // CE
      if (ptItem.ce !=null && ptItem.ce != '' && this.ceList.indexOf(ptItem.ce) === -1) {
        this.ceList.push(ptItem.ce);
      }
      // Client
      if (ptItem.client!=null && ptItem.client != '' && this.clientList.indexOf(ptItem.client) === -1) {
        this.clientList.push(ptItem.client);
      }
      // Service Line
      if (ptItem.serviceLine!=null && ptItem.serviceLine != '' && this.serviceLineList.indexOf(ptItem.serviceLine) === -1) {
        this.serviceLineList.push(ptItem.serviceLine);
      }
      // Project Manager
      if (ptItem.projectManager!=null && ptItem.projectManager != '' && this.projectManagerList.indexOf(ptItem.projectManager) === -1) {
        this.projectManagerList.push(ptItem.projectManager);
      }
      // Project Code
      if (ptItem.projectCode!=null && ptItem.projectCode != '' && this.projectCodeList.indexOf(ptItem.projectCode) === -1) {
        this.projectCodeList.push(ptItem.projectCode);
      }
      // Project Name
      if (ptItem.projectName!=null && ptItem.projectName != '' && this.projectNameList.indexOf(ptItem.projectName) === -1) {
        this.projectNameList.push(ptItem.projectName);
      }
      // Project Type
      if (ptItem.projectType!=null && ptItem.projectType != '' && this.projectTypeList.indexOf(ptItem.projectType) === -1) {
        this.projectTypeList.push(ptItem.projectType);
      }
    });
    // Orden alfabetico
    this.sectorList.sort((a, b) => a == null ? 0 : a.localeCompare(b));
    this.unList.sort((a, b) => a == null ? 0 :a.localeCompare(b));
    this.ceList.sort((a, b) => a == null ? 0 :a.localeCompare(b));
    this.clientList.sort((a, b) => a == null ? 0 :a.localeCompare(b));
    this.serviceLineList.sort((a, b) => a == null ? 0 :a.localeCompare(b));
    this.projectManagerList.sort((a, b) => a == null ? 0 :a.localeCompare(b));
    this.projectCodeList.sort((a, b) => a == null ? 0 :a.localeCompare(b));
    this.projectNameList.sort((a, b) => a == null ? 0 :a.localeCompare(b));
    this.projectTypeList.sort((a, b) => a == null ? 0 :a.localeCompare(b));
    this.chargeFilters();
  }

  chargeFilters() {
    this.filterProjectTracking = this.projectTrackingFilterService.getFilters();
    if (this.filterProjectTracking !== undefined) {
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

  searchByFilters() {
    this.projectTrackingsFilter = Array<ProjectTracking>();
    let eqSector: boolean = false;
    let eqUN: boolean = false;
    let eqCE: boolean = false;
    let eqClient: boolean = false;
    let eqServiceLine: boolean = false;
    let eqProjectManager: boolean = false;
    let eqProjectCode: boolean = false;
    let eqProjectName: boolean = false;
    let eqProjectType: boolean = false;

    // Si no existen filtros ingresados no se realizara busqueda
    if (this.filterProjectTracking.sector == "" &&
      this.filterProjectTracking.un == "" &&
      this.filterProjectTracking.ce == "" &&
      this.filterProjectTracking.client == "" &&
      this.filterProjectTracking.serviceLine == "" &&
      this.filterProjectTracking.projectManager == "" &&
      this.filterProjectTracking.projectCode == "" &&
      this.filterProjectTracking.projectName == "" &&
      this.filterProjectTracking.projectType == "") {
      return;
    }

    this.projectTrackingFilterService.setFilters(this.filterProjectTracking);

    // Filtro por unidad
    this.projectTrackings.forEach(ptItem => {
      eqSector = false;
      eqUN = false;
      eqCE = false;
      eqClient = false;
      eqServiceLine = false;
      eqProjectManager = false;
      eqProjectCode = false;
      eqProjectName = false;
      eqProjectType = false;
      // Filtro Sector
      if (this.filterProjectTracking.sector == "" || this.filterProjectTracking.sector === ptItem.sector) {
        eqSector = true;
      }

      // Filtro UN
      if (this.filterProjectTracking.un == "" || this.filterProjectTracking.un === ptItem.un) {
        eqUN = true;
      }

      // Filtro CE
      if (this.filterProjectTracking.ce == "" || this.filterProjectTracking.ce === ptItem.ce) {
        eqCE = true;
      }

      // Filtro Client
      if (this.filterProjectTracking.client == "" || this.filterProjectTracking.client === ptItem.client) {
        eqClient = true;
      }

      // Filtro Service Line
      if (this.filterProjectTracking.serviceLine == "" || this.filterProjectTracking.serviceLine === ptItem.serviceLine) {
        eqServiceLine = true;
      }

      // Filtro Project Manager
      if (this.filterProjectTracking.projectManager == "" || this.filterProjectTracking.projectManager === ptItem.projectManager) {
        eqProjectManager = true;
      }

      // Filtro Project Code
      if (this.filterProjectTracking.projectCode == "" || this.filterProjectTracking.projectCode === ptItem.projectCode) {
        eqProjectCode = true;
      }
      // Filtro Project Name
      if (this.filterProjectTracking.projectName == "" || this.filterProjectTracking.projectName === ptItem.projectName) {
        eqProjectName = true;
      }

      // Filtro Project Type
      if (this.filterProjectTracking.projectType == "" || this.filterProjectTracking.projectType === ptItem.projectType) {
        eqProjectType = true;
      }

      // Check flags
      if (eqSector && eqUN && eqCE && eqClient && eqServiceLine && eqProjectManager && eqProjectCode && eqProjectName && eqProjectType) {
        this.projectTrackingsFilter.push(ptItem);
      }
    });

    // Recarga de lista
    this.projectTrackingsFilter.sort((a, b) => a.alarm.level.toString().localeCompare(b.alarm.level.toString()));
    this.loadPaginator(this.projectTrackingsFilter);
    this.projectTrackingsExport = this.projectTrackingsFilter;
    this.totalResults = this.totalRows;

  }

  get totalRows(): number {
    return this.projectTrackingsFilter.length;
  }

  filterClean: boolean = false;

  limpiar() {
    this.filterProjectTracking = new ProjectTrackingFilter('', '', '', '', '', '', '', '', '', null, null);
    this.filterClean = !this.filterClean;
    this.projectTrackingFilterService.setFilters(this.filterProjectTracking);
    // Recarga de lista
    this.projectTrackingsFilter.sort((a, b) => a.alarm.level.toString().localeCompare(b.alarm.level.toString()));
    this.loadPaginator(this.projectTrackings);
    this.projectTrackingsExport = this.projectTrackings;
    this.totalResults = null;



    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  prepareDoubt(projectCode: string) {
    Swal.fire({
      title: projectCode + ' </br> Favor detalle su consulta',
      input: 'text',
      inputAttributes: {
        autocapitalize: 'off'
      },
      showCancelButton: false,
      confirmButtonText: 'Enviar',
      showLoaderOnConfirm: true,
      inputValidator: (duda) => {
        if (!duda) {
          return `Debe ingresar su duda`;
        }
      },
      preConfirm: (duda) => {
        return this.doubtsService.sendDoubt(new Doubt(duda, projectCode, localStorage.getItem("currentUser"))).toPromise()
          .then(response => {
            return true;
          }, (error) => {
            Swal.showValidationMessage(
              `Error al enviar duda, favor intentar mas tarde`
            );
            return false;
          }
          )
      },
      allowOutsideClick: () => !Swal.isLoading()
    }).then((response) => {
      if (response.value == true) {
        Swal.fire(
          '¡ Consulta enviada !',
          'Su consulta fue recepcionada.',
          'success'
        )
      };
    })
  }

  deleteProjectTracking(row) {
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
        this.projectTrackingService.deleteById(row.projectCode).subscribe(
          (response) => {
            this.getAllProjectTracking();
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



  async upload() {
    const { value: option } = await Swal.fire({
      title: 'Favor eliga una opción de carga masiva',
      input: 'select',
      inputOptions: this.projectTrackingService.uploadOptions,
      inputPlaceholder: 'Seleccionar',
      showCancelButton: true,
      inputValidator: (value) => {
        return new Promise((resolve) => {
          if (value == null || value === "") {
            resolve('Favor elegir una de las opciones')
          }
          else resolve();
        })
      }
    });
    if (option) {
      const fileUpload = this.fileUpload.nativeElement;
      fileUpload.onchange = () => {
        let files = [];
        for (let index = 0; index < fileUpload.files.length; index++) {
          const file = fileUpload.files[index];
          files.push({ data: file, inProgress: false, progress: 0 });
        }
        this.uploadFiles(files, option);
      };
      fileUpload.click();
    }
  }

  private uploadFiles(files, option: number) {
    this.fileUpload.nativeElement.value = '';
    files.forEach(file => {
      this.uploadFile(file, option);
    });
  }

  uploadFile(file, option: number) {
    const formData = new FormData();
    formData.append('file', file.data);

    // Se abre PopUp
    Swal.fire({
      title: 'Cargando archivo',
      text: 'Esto puede tardar tiempo',
      allowEscapeKey: false,
      allowOutsideClick: false,
      showConfirmButton: false,
      onOpen: () => {
        Swal.showLoading();
      }
    });

    file.inProgress = true;
    this.projectTrackingService.upload(option, formData).subscribe(
      (rr) => {
        Swal.fire({
          icon: 'success',
          title: '¡Archivo cargado!',
          showConfirmButton: true
        })
        this.getAllProjectTracking();
      }, (err) => {
        Swal.fire({
          icon: 'error',
          title: 'Ha ocurrido un error',
          text: 'Verifique la información contenida en el archivo'
        })
        console.log(err);
      });
  }
  formatBoolean(opc: boolean): string {
    return opc ? "Si" : "No";
  }

  exportAsXLSX() {
    let dataExport: any[] = [];
    this.projectTrackingsExport.forEach(ptItem => {
      dataExport.push(this.createCellExport(ptItem));
    });
    this.excelProjectService.exportAsExcelFile(dataExport, 'resultProjectTracking');
  }

  createCellExport(ptItem: ProjectTracking) {
    return {
      // General
      'Sector': ptItem.sector,
      'UN': ptItem.un,
      'CE': ptItem.ce,
      'Cliente': ptItem.client,
      'Línea Servicio': ptItem.serviceLine,
      'Gerente de Proyecto': ptItem.projectManager,
      'Código Proyecto': ptItem.projectCode,
      'Nombre de Proyecto': ptItem.projectName,
      'Tipología de Proyecto': ptItem.projectType,
      'Fecha inicio Proyecto': ptItem.startDate ? formatDate(ptItem.startDate, 'dd/MM/yyyy', 'en_US') : '',
      'Fecha fin Proyecto': ptItem.endDate ? formatDate(ptItem.endDate, 'dd/MM/yyyy', 'en_US') : '',
      'Fecha real fin Proyecto': ptItem.realEndDate ? formatDate(ptItem.realEndDate, 'dd/MM/yyyy', 'en_US') : '',

      // Proyecto Tradicional
      'Fase del proyecto en el que se encuentra': ptItem.traditional.currentProjectPhase,
      '% de Avance Real del Proyecto': ptItem.traditional.actualPercentage,
      '% de Avance Planificado del Proyecto': ptItem.traditional.plannedPercentage,
      'Hito Relevante 1': ptItem.traditional.relevantMilestoneOne,
      'Fecha de Hito 1': ptItem.traditional.milestoneDateOne ? formatDate(ptItem.traditional.milestoneDateOne, 'dd/MM/yyyy', 'en_US') : '',
      '% de Avance Real Hito 1': ptItem.traditional.actualProgressPercentageOne,
      '% de Avance Planificado Hito 1': ptItem.traditional.compliancePercentageOne,
      'Hito Relevante 2': ptItem.traditional.relevantMilestoneTwo,
      'Fecha de Hito 2': ptItem.traditional.milestoneDateTwo ? formatDate(ptItem.traditional.milestoneDateTwo, 'dd/MM/yyyy', 'en_US') : '',
      '% de Avance Real Hito 2': ptItem.traditional.actualProgressPercentageTwo,
      '% de Avance Planificado Hito 2': ptItem.traditional.compliancePercentageTwo,
      'Hito Relevante 3': ptItem.traditional.relevantMilestoneThree,
      'Fecha de Hito 3': ptItem.traditional.milestoneDateThree ? formatDate(ptItem.traditional.milestoneDateThree, 'dd/MM/yyyy', 'en_US') : '',
      '% de Avance Real Hito 3': ptItem.traditional.actualProgressPercentageThree,
      '% de Avance Planificado Hito 3': ptItem.traditional.compliancePercentageThree,
      'Hito Relevante 4': ptItem.traditional.relevantMilestoneFour,
      'Fecha de Hito 4': ptItem.traditional.milestoneDateFour ? formatDate(ptItem.traditional.milestoneDateFour, 'dd/MM/yyyy', 'en_US') : '',
      '% de Avance Real Hito 4': ptItem.traditional.actualProgressPercentageFour,
      '% de Avance Planificado Hito 4': ptItem.traditional.compliancePercentageFour,
      'Hito Relevante 5': ptItem.traditional.relevantMilestoneFive,
      'Fecha de Hito 5': ptItem.traditional.milestoneDateFive ? formatDate(ptItem.traditional.milestoneDateFive, 'dd/MM/yyyy', 'en_US') : '',
      '% de Avance Real Hito 5': ptItem.traditional.actualProgressPercentageFive,
      '% de Avance Planificado Hito 5': ptItem.traditional.compliancePercentageFive,
      // Servicio
      'Total de estimaciones solicitadas en el periodo': ptItem.service.totalEstRequested,
      'Total de requerimientos solicitados en el periodo': ptItem.service.totalReqRequested,
      'Nº de estimaciones en plazo en el periodo': ptItem.service.numEstDelivered,
      'Nº de requerimiento en plazo en el periodo': ptItem.service.numReqDelivered,
       // Agile
      'N° de historias terminadas en el Sprint': ptItem.numFinishedSprintStories,
      'N° de Releases': ptItem.numReleases,
      'N° de Sprints': ptItem.numSprints,
      'N° de historias planificadas en el Sprint': ptItem.numSprintPlannedHistories,
      //modificaciones
        'Ultima modificación':ptItem.modification? formatDate(ptItem.modification.lastModified, 'dd/MM/yyyy', 'en_US') : '',
        'Usuario modificación':ptItem.modification? ptItem.modification.username : ''
    };
  }
}