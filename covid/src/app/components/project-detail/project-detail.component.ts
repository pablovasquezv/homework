import { Component, OnInit, ViewChild, ChangeDetectorRef } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { ProjectTracking } from 'src/app/models/ProjectTracking';
import { ProjectTrackingService } from 'src/app/services/projectTracking.service';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2'
import { DatePipe } from '@angular/common';
import * as moment from 'moment';
import { ProfileService } from 'src/app/services/profile.service';
import { Profile } from 'src/app/models/Profile';
import { MatPaginator } from '@angular/material/paginator';
import { Rol } from 'src/app/models/Rol';
import { MatTableDataSource } from '@angular/material/table';
import { Funding } from 'src/app/models/Funding';
import { Survay } from 'src/app/models/Survay';
import { ProjectProfile } from 'src/app/models/ProjectProfile';
import { ChangeLogComponent } from './change-log/change-log.component';
import { MatSort } from '@angular/material/sort';
import { MatExpansionPanel } from '@angular/material/expansion';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { ServiceFields } from 'src/app/models/ServiceFields';
import { TraditionalFields } from 'src/app/models/TraditionalFields';
import { StaffFields } from 'src/app/models/StaffFields';
import { HistoryTabComponent } from './history-tab/history-tab.component';
import { TechnicalFields } from 'src/app/models/TechnicalFields';
import { MatTabChangeEvent } from '@angular/material/tabs';


interface Data {
  id: number;
  value: string;
}

@Component({
  selector: 'app-project-detail',
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.scss']
})
export class ProjectDetailComponent implements OnInit {

  projectTracking: ProjectTracking = new ProjectTracking();
  modoEdicion = false;
  projectProfile: ProjectProfile = new ProjectProfile();
  agile = false;
  newDetail: ProjectProfile = new ProjectProfile();
  service = false;
  tradicional = false;
  //editText: string = "Editar";
  toDelete: ProjectProfile[]=[];

  // Alert
  alertAgile: number = 3;
  alertHito1: number = 3;
  alertHito2: number = 3;
  alertHito3: number = 3;
  alertHito4: number = 3;
  alertHito5: number = 3;
  alertHitoGeneral: number = 3;
  alertServiceReq: number = 3;
  alertServiceEst: number = 3;

  count: number = 0;
  buttonDisabled: boolean = false;

  displayedColumns: string[] = ['profileName', 'tarifa', 'opt']
  dataSource: MatTableDataSource<ProjectProfile>;

 // isDisabled = true;
  initial_realEndDate: string;
  initial_extensionReason: string;
  initial_extAgainstIncome: boolean;

  projectTypes: Data[] = [
    { id: 1, value: "Servicios" },
    { id: 2, value: "Proyecto Ágil" },
    { id: 3, value: "Proyecto Tradicional" }
  ];

  projectLines: string[] = [
    "ADM",
    "BPO",
    "TEC DIG EXPERIENCE",
    "AGILE",
    "BUSINESS",
    "SALESFORCE",
    "STAFF AUGMENTATION",
    "AMS",
    "PMO",
    "IM",
    "SOLUTIONS",
    "EHCOS",
    "PROYECTOS",
    "TRANSFORMATION AGILE",
    "STRATEGIC VALUE",
    "TEC DIG ARCHITECTURE",
    "TEC DIG INTELLIGENCE",
    "TESTING",
    "OPERATIONS STRATEGY",
    "TALENT & TRANSFORMATION",
    "CUSTOMER STRATEGY"
  ];

  public domain: string = "SEGUIMIENTO_PROYECTOS";
  profile: Profile = null;
  public paginatorSize: number = 5;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild('changeLog') changeLog: ChangeLogComponent;
  @ViewChild('historyTab') historyTab: HistoryTabComponent;
    @ViewChild(MatSort, { static: true }) sort: MatSort;

  //detailTab: FormGroup = new FormGroup({}, {});
  
  tradicionalTab: FormGroup = new FormGroup({}, {});
  agileTab: FormGroup = new FormGroup({}, {});
  serviceTab: FormGroup = new FormGroup({}, {});

  extensionTab: FormGroup = new FormGroup({}, {});
  staffTab: FormGroup = new FormGroup({}, {});
  newStaffTab:FormGroup = new FormGroup({}, {});
  rateTypeInput = new FormControl('', Validators.required);
  
  newProfileTab: FormGroup = new FormGroup({}, {});
  cellTab: FormGroup = new FormGroup({}, {});

  constructor(private projectTrackingService: ProjectTrackingService,
    private route: ActivatedRoute,
    private router: Router,
    private datePipe: DatePipe,
    private profileService: ProfileService,
    private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {

    if (localStorage.getItem("pt_profiles_pageSize") != null)
      this.paginatorSize = parseInt(localStorage.getItem("pt_profiles_pageSize"));
    
    this.edit();
    this.loadProfile();
    
    
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  deliveryAlarm(): string {
    let alarm = "material-icons color_gray";
    if (this.projectTracking != null && this.projectTracking.survayReport != null &&
      this.projectTracking.survayReport.actionPlane!=null &&
      this.projectTracking.survayReport.actionPlane.toUpperCase() == "SI") {
      alarm = "material-icons color_red"
    }
    return alarm;
  }

  staffAlarm(): string {
    let alarm = "material-icons color_gray";
    if (this.hideProfilesTable() == false)
      alarm = "material-icons color_green";
    return alarm;
  }

  getAlarmClass(level: number): string {
    if (level == null) return "";

    if (level == 1) return "material-icons color_red";
    if (level == 2) return "material-icons color_yellow";
    if (level == 3) return "material-icons color_green";
    if (level == 4) return "material-icons color_gray";

    return "";
  }

  isNullOrEmpty(value: string): Boolean {
    if (value == null) return true;
    if (value === "") return true;
    return false;
  }

  isValidDetail(detail: ProjectProfile): Boolean {
    this.evaluateFormGroup(this.newProfileTab);

    if (this.isNullOrEmpty(detail.profileName) || detail.tarifa == null)
      return false;
    else
      return this.newProfileTab.valid;
  }

  clean() {
    this.newDetail = new ProjectProfile(this.projectTracking.projectCode);
  }

  loadPaginator(data: Array<ProjectProfile>) {
    this.dataSource = new MatTableDataSource(data);
    this.dataSource.paginator = this.paginator;
    this.paginator._intl.itemsPerPageLabel = 'Registros por página.'
    this.dataSource.sortingDataAccessor = (item, property) => {
      switch (property) {
        case 'profileName': return item.profileName;
        case 'tarifa': return item.tarifa;
        default: return item[property];
      }
    };
    this.dataSource.sort = this.sort;
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

  isAdminProfile(): boolean {
    if (this.profile == null || this.profile.roles == null) return false;

    for (var i = 0; i < this.profile.roles.length; i++) {
      if (this.profile.roles[i].name.toLowerCase() === "administrador")
        return true;
    }
    return false;
  }

  mustEnableAccess(): boolean {
    if (this.profile == null || this.profile.roles == null) return false;

    for (var i = 0; i < this.profile.roles.length; i++) {
      if ((this.profile.roles[i].name.toUpperCase() === "EJECUTIVO") ||
        (this.profile.roles[i].name.toUpperCase() === "ADMINISTRADOR"))
        return true;
    }
  }

  deleteProfile(profile: ProjectProfile) {
    const objIndex = this.projectTracking.staff.detailProjectProfileList.findIndex((obj: ProjectProfile) => { return obj == profile });
    let deleted = null;
    if (objIndex > -1) {
      deleted = this.projectTracking.staff.detailProjectProfileList.splice(objIndex, 1);
    }
    if (deleted != null) {
      deleted.forEach(detail => {
        this.toDelete.push(detail);
      });
    }

    this.loadPaginator(this.projectTracking.staff.detailProjectProfileList);
  }

  hideProfilesTable(): boolean {
    if (this.projectTracking.staff.detailProjectProfileList == null || this.projectTracking.staff.detailProjectProfileList.length == 0)
      return true;
    else return false;
  }

  edit() {
    const project = this.route.snapshot.paramMap.get('project');
    this.modoEdicion = false;
    if (project != null) {
      this.projectTrackingService.findById(project).subscribe(
        (response) => {
          this.projectTracking = response;
          Object.setPrototypeOf(this.projectTracking, ProjectTracking.prototype);

          if (this.projectTracking.service == null)
            this.projectTracking.service = new ServiceFields();
          if (this.projectTracking.traditional == null)
            this.projectTracking.traditional = new TraditionalFields();
          if (this.projectTracking.staff == null)
            this.projectTracking.staff = new StaffFields();
            if (this.projectTracking.technical == null)
            this.projectTracking.technical = new TechnicalFields();
          if (this.projectTracking.fundingReport == null)
            this.projectTracking.fundingReport = new Funding(this.projectTracking.projectCode);
          if (this.projectTracking.survayReport == null)
            this.projectTracking.survayReport = new Survay(this.projectTracking.projectCode);
          
          if (this.validateServiceLine('STAFF AUGMENTATION')) {
            if (this.projectTracking.staff.detailProjectProfileList == null) {
              this.projectTracking.staff.detailProjectProfileList = [];
            }
            else
              this.loadPaginator(this.projectTracking.staff.detailProjectProfileList);
          }
          this.loadProjectType();
          this.calculateAgile();
          this.loadHitosAlert();
          this.loadServiceAlert();
          this.initial_realEndDate = moment(this.projectTracking.realEndDate).format('DDMMYYYY');
          this.initial_extensionReason = this.projectTracking.extensionReason;
          this.initial_extAgainstIncome = this.projectTracking.extAgainstIncome;
         
          this.validateForm();
          //this.evaluateFormGroup(this.newProfileTab);
        }, (error) => {
          console.log(error);
        }
      );
      this.modoEdicion = true;
    }
  }
  
  tabChange(tabChangeEvent: MatTabChangeEvent): void {
    console.log('tabChangeEvent => ', tabChangeEvent);
    console.log('index => ', tabChangeEvent.index);

    if("Histórico" === tabChangeEvent.tab.textLabel){
      this.loadHistoryTab();
    }
  }

  loadHistoryTab(){
    console.log("LOAD HISTORY");
    this.historyTab.loadData(this.projectTracking);
  }

  handlePaginatorEvent(event: any) {
    this.paginatorSize = event.pageSize;
    localStorage.setItem("pt_profiles_pageSize", this.paginatorSize + "");
  }

  loadAgileAlert() {
    this.alertAgile = 3;
    if (this.projectTracking.sprintComplInd == null) {
      this.alertAgile = 4;
    }
    else if (this.projectTracking.sprintComplInd != null && this.projectTracking.sprintComplInd.toUpperCase() == 'NO CUMPLE') {
      this.alertAgile = 1;
    }
  }

  loadHitosAlert() {
    this.alertHito1 = this.setHitoAlertLevel(this.projectTracking.traditional.deviationPercentageOne);
    this.alertHitoGeneral = this.setAlertLevel(this.projectTracking.traditional.deviationPercentage);
    this.alertHito2 = this.setHitoAlertLevel(this.projectTracking.traditional.deviationPercentageTwo);
    this.alertHito3 = this.setHitoAlertLevel(this.projectTracking.traditional.deviationPercentageThree);
    this.alertHito4 = this.setHitoAlertLevel(this.projectTracking.traditional.deviationPercentageFour);
    this.alertHito5 = this.setHitoAlertLevel(this.projectTracking.traditional.deviationPercentageFive);
  }

  setAlertLevel(deviation: number): number {
    let alert = 4;
    if (deviation == null) return alert;
    if (deviation >= 10) {
      alert = 1;
    }
    else if (deviation > 5 && deviation < 10)
      alert = 2;
    else alert = 3;
    return alert;
  }

  setHitoAlertLevel(deviation: number): number {
    let alert = 4;
    if (deviation == null) return alert;
    if (deviation >= 10) {
      alert = 2;
    }
    else alert = 3;
    return alert;
  }

  loadServiceAlert() {
    this.alertServiceReq = 3;
    this.alertServiceEst = 3;
    let yellow = this.projectTracking.yellowMargin;
    let red = this.projectTracking.redMargin;

    // red
    // red.bottom<=X<red.top => alarma red
    // yellow
    // yellow.bottom<=X<yellow.top => alarma yellow

    if (this.projectTracking.service.percentageReqResolved == null) {
      this.alertServiceReq = 4;
    }
    else if (this.projectTracking.service.percentageReqResolved !== null &&
      red.bottom <= this.projectTracking.service.percentageReqResolved && this.projectTracking.service.percentageReqResolved < red.top) {
      this.alertServiceReq = 1;
    }
    if (this.projectTracking.service.percentageEstDelivered == null) {
      this.alertServiceEst = 4;
    }
    else if (this.projectTracking.service.percentageEstDelivered !== null &&
      red.bottom <= this.projectTracking.service.percentageEstDelivered && this.projectTracking.service.percentageEstDelivered < red.top) {
      this.alertServiceEst = 1;
    }

    if (this.projectTracking.service.percentageReqResolved !== null &&
      yellow.bottom <= this.projectTracking.service.percentageReqResolved && this.projectTracking.service.percentageReqResolved < yellow.top) {
      this.alertServiceReq = 2;
    }

    if (this.projectTracking.service.percentageEstDelivered !== null &&
      yellow.bottom <= this.projectTracking.service.percentageEstDelivered && this.projectTracking.service.percentageEstDelivered < yellow.top) {
      this.alertServiceEst = 2;
    }
  }

  validateForm():boolean{
    if (this.evaluateFormGroup(this.extensionTab) == false)
      return false;

    if (this.validateServiceLine('STAFF AUGMENTATION')) {
      if (this.hideProfilesTable() == false) {
        this.staffTab.addControl("RateTypeControl", this.rateTypeInput);
      }
      return this.evaluateFormGroup(this.staffTab);
    }
    else if(this.tradicional){
      return this.evaluateFormGroup(this.tradicionalTab);
    }
    else if(this.agile){
      return this.evaluateFormGroup(this.agileTab);
    }

    return true;
  }

  setExtension(option: boolean) {
    this.projectTracking.extAgainstIncome = option;
  }

  showContraIngresos(): boolean {
    let realEnd = moment(this.projectTracking.realEndDate);
    let end = moment(this.projectTracking.endDate);
    let mustEnableExtension = (realEnd > end);
    this.projectTracking.declaredExtension = mustEnableExtension;
    return mustEnableExtension;
  }

  //Valida si alguno de los campos de la extension fueron modificados a los iniciales, si es asi retorna true, caso contrario false.
  checkIfExtensionChanged() {
    let actualEnd = moment(this.projectTracking.realEndDate).format('DDMMYYYY');
    if (this.initial_realEndDate != actualEnd || this.initial_extensionReason != this.projectTracking.extensionReason)
      return true;
    else
      return false;
  }

  agregarPerfil(panel: MatExpansionPanel) {

    if (this.evaluateFormGroup(this.newStaffTab) == false ){//|| this.isValidDetail(this.newDetail) == false) {
      Swal.fire({
        title: '<strong>Campos faltantes</strong>',
        icon: 'info',
        html:
          'Favor de revisar los campos obligatorios',
        showCloseButton: true,
        focusConfirm: true,
        confirmButtonText: 'Entendido!'
      });
      return;
    }
    this.newDetail.projectCode = this.projectTracking.projectCode;
    this.projectTracking.staff.detailProjectProfileList.push(this.newDetail);
    this.clean();
    this.loadPaginator(this.projectTracking.staff.detailProjectProfileList);
    Swal.fire({
      title: 'Añadido',
      text: 'Perfil añadido al plan',
      allowEscapeKey: true,
      allowOutsideClick: false,
      showConfirmButton: true,
      timer: 1000
    });
    panel.close();
  }

  needOK(validateChanges: boolean = true): boolean {
    //si el proyecto no tiene extension declarada, entonces no requiere OK
    if (this.projectTracking.declaredExtension == false)
      return false;

    //si el proyecto ya fue validado  y se modificaron alguno de sus campos, entonces requiere nueva validacion
    if (validateChanges) {
      if (this.projectTracking.extensionValidated && this.checkIfExtensionChanged())
        return true;
    }

    //si la extension no fue validada, entonces requiere validacion
    if (this.projectTracking.extensionValidated == false || this.projectTracking.extensionValidated == null)
      return true;

    //caso contrario no requiere validacion.
    else return false;
  }

  transformDate(date) {
    this.datePipe.transform(date, 'yyyy-MM-dd');
  }

  disableByAttemps():Boolean{
    let disable=false;
    if (this.projectTracking.attempts >= 3)
      disable = true;
    if (this.isAdminProfile()) disable = false;
    return disable
  }

  aprobarExtension() {
    Swal.fire({
      title: '',
      icon: 'warning',
      html: '¿ Confirma aprobar la extensión ?',
      showCloseButton: false,
      showCancelButton: true,
      focusConfirm: true,
      confirmButtonText: 'Aprobar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.value) {
        this.projectTracking.extensionValidated = true;
        this.projectTrackingService.updateProjectTracking(this.projectTracking.projectCode, this.projectTracking).subscribe(
          (response) => {
            Swal.fire('Aprobado', 'La extensión fue aprobada', 'success');
          }, (error) => {
            console.log(error);
            Swal.fire('Error', 'Ha ocurrido un error, favor intentar nuevamente', 'error');
            this.projectTracking.extensionValidated = false;
          });
      }
      else if (result.dismiss === Swal.DismissReason.cancel) {
      }
    })
  }

  
  async guardar() {
    if (this.service) {
      let required = false;
      let delivered = false;

      if (this.projectTracking.service.totalReqRequested < this.projectTracking.service.numReqDelivered)
        required = true;
        if (this.projectTracking.service.totalEstRequested < this.projectTracking.service.numEstDelivered)
        delivered = true;

      if ((required || delivered) && this.validateServiceLine('STAFF AUGMENTATION') == false) {
        Swal.fire(
          'Favor revisar la información ingresada',
          'Los requerimientos/estimaciones entregados en plazo debe ser menor o igual a los solicitados',
          'warning'
        );
        return;
      }
    }
    
    if (this.valida() === true) {
      if (this.modoEdicion === false) {

        this.projectTracking.extensionValidated = !this.needOK();
        this.projectTrackingService.guardar(this.projectTracking).subscribe(
          (response) => {
            Swal.fire(
              '¡Guardado!',
              'El proyecto ha sido guardado con éxito.',
              'success'
            );
            this.router.navigate(['app/editProject/' + this.projectTracking.projectCode]);
          }, (error) => {
            console.log(error);
            if (error.error.message == "Invalid Update - max attemps") {
              Swal.fire(
                '',
                'Ha superado el limite maximo de modificaciones',
                'error'
              );
            }
            else
              this.router.navigate(['//']);
          }
        );
      } else {
        if (this.toDelete != null && this.toDelete.length > 0) {
          let result = await this.projectTrackingService.deleteProfile(this.toDelete).toPromise();
          if (result == false) {
            Swal.fire(
              'Error!',
              'Error al eliminar los perfiles',
              'warning'
            );
            return;//romper ciclo si operacion fallo
          }
          else
            this.toDelete = [];
        }
              
        this.projectTracking.extensionValidated = !this.needOK();
        this.projectTrackingService.updateProjectTracking(this.projectTracking.projectCode, this.projectTracking).subscribe(
          (response) => {
            Swal.fire(
              '¡Actualizado!',
              'El proyecto ha sido actualizado con éxito.',
              'success'
            );
            if (this.changeLog != null)
              this.changeLog.reload();
            this.edit();
          }, (error) => {
            console.log(error);
            if (error.error.message == "Invalid Update - max attemps") {
              Swal.fire(
                '',
                'Ha superado el limite maximo de modificaciones',
                'error'
              );
            }
            else
              this.router.navigate(['//']);
          }
        );
      }
    }
    else {
      Swal.fire({
        title: '<strong>Campos faltantes</strong>',
        icon: 'info',
        html:
          'Favor de revisar formato de los datos y campos obligatorios',
        showCloseButton: true,
        focusConfirm: true,
        confirmButtonText: 'Entendido!'
      })
    }
  }

  evaluateFormGroup(form: FormGroup): boolean {
    let valid = false;
    if (form != null) {
      Object.keys(form.controls).forEach(key => {
        form.controls[key].markAsDirty();
        if (form.controls[key].valid == false)
          console.log("invalid " + key);
      });
    }

    return form.valid;
  }

  valida() {
    let ok = true;
    //ok = ok && this.homework.cliente != null;
    //ok = ok && this.homework.cliente !== '';
    //ok = ok && this.homework.usuario != null;
    //ok = ok && this.homework.nombre != null;
    //ok = ok && this.homework.nombre !== '';
    //ok = ok && this.homework.donde != null;
    /*
        if (this.projectTracking.declaredExtension && !this.projectTracking.extAgainstIncome &&
          (this.projectTracking.extensionReason == null || this.projectTracking.extensionReason == "")) {
          ok = ok && false;
          if (this.motivo != null) this.motivo.nativeElement.focus();
        }*/

    /*
    this.projectTracking.detailProjectProfileList.forEach(detail => {
      if (!this.isValidDetail(detail))
          ok=false;
    });*/

    ;

    return this.validateForm();
  }


  reloadType(event) {

    this.projectTracking.projectType = this.projectTypes.find((o) => { return o.id == event.value }).value;
    this.loadProjectType();
  }


  loadProjectType() {
    this.showSection(this.projectTracking.projectTypeCode);
  }

  showSection(projectTypeCode: number) {
    this.agile = false;
    this.service = false;
    this.tradicional = false;

    if (1 === projectTypeCode) {
      this.service = true;
    }
    if (2 === projectTypeCode) {
      this.agile = true;
    }

    if (3 === projectTypeCode) {
      this.tradicional = true;
    }
  }

  calculateServiceValues() {
    // % de estimaciones entregadas en plazo (estimaciones: incidentes/evolutivos)
    this.projectTracking.service.percentageEstDelivered = this.calculatePercEstimationDelivered(this.projectTracking);
    // % de requerimientos resueltos en plazo (requerimientos: incidentes/evolutivos)
    this.projectTracking.service.percentageReqResolved = this.calculatePercRequirementResolved(this.projectTracking);
    // % de incidentes vs total requerimientos solicitados

    this.loadServiceAlert();
  }


  calculatePercEstimationDelivered(pt: ProjectTracking): number {
    if ((pt.service.totalEstRequested == null || pt.service.totalEstRequested == 0) || pt.service.numEstDelivered == null) {
      return null;
    }
    let value = pt.service.numEstDelivered / pt.service.totalEstRequested;

    return value * 100;
  }

  calculatePercRequirementResolved(pt: ProjectTracking): number {
    if ((pt.service.totalReqRequested == null || pt.service.totalReqRequested == 0) || pt.service.numReqDelivered == null) {
      return null;
    }
    let value = pt.service.numReqDelivered / pt.service.totalReqRequested;
    return value * 100;
  }



  calculateWIPyNRN(f: Funding): number {
    if ((f.wip == 0 || f.nrn == 0)) {
      return 0;
    }
    let value = f.wip / f.nrn
    return value;
  }

  calculateAgile() {
    if ((this.projectTracking.numSprints == null || this.projectTracking.numSprints == 0) || this.projectTracking.numFinishedSprintStories == null) {
      this.projectTracking.teamSpeedAverage = 0;
    }
    else {
      this.projectTracking.teamSpeedAverage = this.projectTracking.numFinishedSprintStories / this.projectTracking.numSprints;
    }
    // Cumpliento de Sprint
    this.calculateSprintComplianceInd();
  }

  calculateSumtarifas() {
    let value: number = 0.0;

    this.projectTracking.staff.detailProjectProfileList.forEach(projectProfile => {
      Object.setPrototypeOf(projectProfile, ProjectProfile.prototype);
      if (projectProfile.tarifa != null)
        value = value + projectProfile.tarifa;
    });
    this.projectTracking.staff.sumTarifas = value;
  } 

  calculateSprintComplianceInd() {
    if (this.projectTracking.numFinishedSprintStories == null || this.projectTracking.numSprintPlannedHistories == null) {
      this.projectTracking.sprintComplInd = null;
    }
    else if (this.projectTracking.numFinishedSprintStories < this.projectTracking.numSprintPlannedHistories)
      this.projectTracking.sprintComplInd = "NO CUMPLE";
    else
      this.projectTracking.sprintComplInd = "CUMPLE";

    this.loadAgileAlert();
  }

  redondearDecimales(valorInicial: number) {
    let parteEntera: number;
    let resultado: number;
    resultado = valorInicial;
    parteEntera = Math.floor(resultado);
    resultado = (resultado - parteEntera) * Math.pow(10, 2);
    resultado = Math.round(resultado);
    resultado = (resultado / Math.pow(10, 2)) + parteEntera;
    return resultado;
  }

  changeHitoGeneral() {

    this.projectTracking.traditional.deviationPercentage = null;
    if (this.projectTracking.traditional.actualPercentage !== null && this.projectTracking.traditional.plannedPercentage !== null) {
      this.projectTracking.traditional.deviationPercentage = this.calculateDetailHito(this.projectTracking.traditional.actualPercentage, this.projectTracking.traditional.plannedPercentage);
    }
    this.loadHitosAlert();
  }

  changeHito1() {
    this.projectTracking.traditional.deviationPercentageOne = null;
    if (this.projectTracking.traditional.actualProgressPercentageOne !== null && this.projectTracking.traditional.compliancePercentageOne !== null) {
      this.projectTracking.traditional.deviationPercentageOne = this.calculateDetailHito(this.projectTracking.traditional.actualProgressPercentageOne, this.projectTracking.traditional.compliancePercentageOne);
    }
    this.loadHitosAlert();
  }

  changeHito2() {
    this.projectTracking.traditional.deviationPercentageTwo = null;
    if (this.projectTracking.traditional.actualProgressPercentageTwo !== null && this.projectTracking.traditional.compliancePercentageTwo !== null) {
      this.projectTracking.traditional.deviationPercentageTwo = this.calculateDetailHito(this.projectTracking.traditional.actualProgressPercentageTwo, this.projectTracking.traditional.compliancePercentageTwo);
    }
    this.loadHitosAlert();
  }

  changeHito3() {
    this.projectTracking.traditional.deviationPercentageThree = null;
    if (this.projectTracking.traditional.actualProgressPercentageThree !== null && this.projectTracking.traditional.compliancePercentageThree !== null) {
      this.projectTracking.traditional.deviationPercentageThree = this.calculateDetailHito(this.projectTracking.traditional.actualProgressPercentageThree, this.projectTracking.traditional.compliancePercentageThree);
    }
    this.loadHitosAlert();
  }

  changeHito4() {
    this.projectTracking.traditional.deviationPercentageFour = null;
    if (this.projectTracking.traditional.actualProgressPercentageFour !== null && this.projectTracking.traditional.compliancePercentageFour !== null) {
      this.projectTracking.traditional.deviationPercentageFour = this.calculateDetailHito(this.projectTracking.traditional.actualProgressPercentageFour, this.projectTracking.traditional.compliancePercentageFour);
    }
    this.loadHitosAlert();
  }

  changeHito5() {
    this.projectTracking.traditional.deviationPercentageFive = null;
    if (this.projectTracking.traditional.actualProgressPercentageFive !== null && this.projectTracking.traditional.compliancePercentageFive !== null) {
      this.projectTracking.traditional.deviationPercentageFive = this.calculateDetailHito(this.projectTracking.traditional.actualProgressPercentageFive, this.projectTracking.traditional.compliancePercentageFive);
    }
    this.loadHitosAlert();
  }

  validateProjectType(expectedType: string): boolean {
    let result = false;
    if (this.projectTracking.projectType && this.projectTracking.projectType.toUpperCase() === expectedType.toUpperCase())
      return true;
    return result;
  }

  validateServiceLine(expectedType: string): boolean {
    let result = false;
    if (this.projectTracking.serviceLine == null) return false;
    if (this.projectTracking.serviceLine.toUpperCase() === expectedType.toUpperCase())
      return true;
    return result;
  }

  calculateDetailHito(actual: number, compliance: number) {
    let value = 0;
    if (actual !== null && compliance !== null) {
      value = compliance - actual;
      if (value < 0) {
        return 0;
      }
    }
    return value;
  }
  calculateRollbackIndex() {
    let value = 0;

    if (this.projectTracking.technical.numRollbakReqPer == null || this.projectTracking.technical.totalReqClosedPeriod == null) value = null;
    else if (this.projectTracking.technical.totalReqClosedPeriod == 0) value = null;
    else value = 100 * this.projectTracking.technical.numRollbakReqPer / this.projectTracking.technical.totalReqClosedPeriod;

    this.projectTracking.technical.rollbackProd = value;
  }

  calculateTestCaseIndex() {
    let value = 0;

    if (this.projectTracking.technical.testCasesPlanned == null || this.projectTracking.technical.testCasesReal == null) value = null;
    else if (this.projectTracking.technical.testCasesPlanned == 0) value = null;
    else value = 100 * this.projectTracking.technical.testCasesReal / this.projectTracking.technical.testCasesPlanned;

    this.projectTracking.technical.testCasesIndex = value;
  }

  calculateProdErrorIndex() {
    let value = 0;

    if (this.projectTracking.technical.totalReqErrorPeriod == null || this.projectTracking.technical.totalReqClosedPeriod == null) value = null;
    else if (this.projectTracking.technical.totalReqClosedPeriod == 0) value = null;
    else value = 100 * this.projectTracking.technical.totalReqErrorPeriod / this.projectTracking.technical.totalReqClosedPeriod;

    this.projectTracking.technical.prodErrorIndex = value;
  }


  calculatePercentageCompletion() {
    let value = 0;

    if (this.projectTracking.closedStories == null || this.projectTracking.storiesInBacklog == null) value = null;
    else if (this.projectTracking.storiesInBacklog == 0) value = null;
    else value = 100 * this.projectTracking.closedStories / this.projectTracking.storiesInBacklog;

    this.projectTracking.percentageCompletion = value;
  }

 
  calculateRemainingSprints(){
    let value = 0;

    if (this.projectTracking.closedStories == null || this.projectTracking.storiesInBacklog == null || this.projectTracking.cycleTime==null) value = null;
    else if (this.projectTracking.cycleTime == 0) value = null;
    else value =  (this.projectTracking.storiesInBacklog - this.projectTracking.closedStories) / this.projectTracking.cycleTime;

    this.projectTracking.remainingSprints = value;
  }

  checkMinMargin(input: HTMLInputElement) {

    let min = parseInt(input.getAttribute("min"));
    let max = parseInt(input.getAttribute("max"));
    let value = parseInt(input.value);

    if (value < min)
      value = min;
    if (value > max)
      value = max;


    this.projectTracking.redMargin.top = value;
    this.projectTracking.yellowMargin.bottom = value;
  }

  checkMaxMargin(input: HTMLInputElement) {

    let min = parseInt(input.getAttribute("min"));
    let max = parseInt(input.getAttribute("max"));
    let value = parseInt(input.value);

    if (value < min)
      value = min;
    if (value > max)
      value = max;

    this.projectTracking.yellowMargin.top = value;
  }
}
