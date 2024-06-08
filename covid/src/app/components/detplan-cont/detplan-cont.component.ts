import { Component, OnInit, ViewChild, ElementRef, Input } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import Swal from 'sweetalert2';
import { DataService } from '../../services/data.service';
import { PlanContService } from 'src/app/services/plancont.service';
import { PlanCont } from 'src/app/models/PlanCont';
import { ActivatedRoute, Router } from '@angular/router';
import { DetPlanCont } from 'src/app/models/DetPlanCont';
import { MatExpansionPanel } from '@angular/material/expansion';
import { ContDocument } from 'src/app/models/ContDocument';


interface SiNoList {
  id: number;
  value: string;
}

@Component({
  selector: 'app-detplan-cont',
  templateUrl: './detplan-cont.component.html',
  styleUrls: ['./detplan-cont.component.scss']
})
export class DetPlanContComponent implements OnInit {

  archivo: any;

  public base64textString: String = "";
  public base64: any;

  planCont: PlanCont = new PlanCont();
  newDetail: DetPlanCont = new DetPlanCont();
  contDocument:ContDocument=new ContDocument();
  toDelete: DetPlanCont[]=[];

  siNoList: SiNoList[] = [
    { id: 1, value: "SI" },
    { id: 0, value: "NO" },
    { id: 2, value: "N/A" }
  ];

  displayedColumns: string[] = ['perfilClave', 'nombrePerfilClave', 'nombreBackup', 'planContingencia',
    'backupActividades', 'backupHerramientas', 'pclaveReunionSeg', 'comunicacionCliEquipo', 'comentarios', 'opt']
  dataSource: MatTableDataSource<DetPlanCont>;

  public paginatorSize: number = 10;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild("fileUpload", { static: false }) fileUpload: ElementRef; files = [];

  constructor(private _dataService: DataService,
    private _planContService: PlanContService,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit() {
    this.getAllPlanCont();

    if (localStorage.getItem("userlist_pageSize") != null)
      this.paginatorSize = parseInt(localStorage.getItem("userlist_pageSize"));
  }

  handlePaginatorEvent(event: any) {
    this.paginatorSize = event.pageSize;
    localStorage.setItem("userlist_pageSize", this.paginatorSize + "");
  }

  isNullOrEmpty(value: string): Boolean {
    if (value == null) return true;
    if (value === "") return true;
    return false;
  }

  isValidDetail(detail: DetPlanCont): Boolean {
    if (this.isNullOrEmpty(detail.perfilClave) ||
      this.isNullOrEmpty(detail.nombrePerfilClave) ||
      this.isNullOrEmpty(detail.nombreBackup))
      return false;
    else
      return true;
  }
  
  clean(){
    this.newDetail=new DetPlanCont(this.planCont.projectCode);
  }

  async guardar() {
    let validData=true;
    this.planCont.detailKeysResourcesList.forEach(detail => {
      if (!this.isValidDetail(detail))
          validData=false;
    });

    if (!validData) {
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
    
    if (this.toDelete != null && this.toDelete.length > 0) {
      let result = await this._planContService.deleteDetail(this.toDelete).toPromise();
      if (result == false) {
        Swal.fire(
          'Error!',
          'Error al eliminar los perfiles',
          'warning'
        );
      }
      else
        this.toDelete=[];
    }    
    
    this._planContService.guardar(this.planCont).subscribe(
      (response) => {
        this.contDocument.projectCode = this.planCont.projectCode;
        this._planContService.loadDocument(this.planCont.projectCode, this.contDocument).subscribe(
          (res)=>{
            this.getAllPlanCont();
            Swal.fire(
              '¡Guardado!',
              'El registro ha sido guardado con éxito.',
              'success'
            );
            this.router.navigate(['app/editPlanCont/' + this.planCont.projectCode]);
          }
        );
        }, (error) => {
        console.log(error);
      }
    );
  }

undoChanges(){
  Swal.fire({
    title: '',
    icon: 'warning',
    html: 'Se perderán los cambios no guardados.</br>¿ Desea continuar ?',
    showCloseButton: false,
    showCancelButton: true,
    focusConfirm: true,
    confirmButtonText: 'Continuar',
    cancelButtonText: 'Cancelar'
  }).then((result) => {
    if (result.value) {
      this.getAllPlanCont();
      Swal.fire('','Acción realizada','success');
    }});
}

  getAllPlanCont() {
    const codigoproyecto = this.route.snapshot.paramMap.get('codprod');
    this._planContService.findById(codigoproyecto).subscribe(
      (res) => {
        this.planCont = res;
        this.newDetail = new DetPlanCont(this.planCont.projectCode);
        this.loadPaginator(this.planCont.detailKeysResourcesList);
      },
      (err) => {
        console.log(err);
        this.router.navigate(['//']);
      });

      this._planContService.getDocument(codigoproyecto).subscribe(
        (res) => {
          console.log("Loaded");
          if(res!=null){
            if(res.name!=null)
              this.contDocument=res;
            }
        },
        (err) => {
          console.log(err);
          this.router.navigate(['//']);
        });
  }
  
  loadPaginator(data: Array<DetPlanCont>) {
    this.dataSource = new MatTableDataSource(data);
    this.dataSource.paginator = this.paginator;
    this.paginator._intl.itemsPerPageLabel = 'Registros por página.'
    this.dataSource.sortingDataAccessor = (item, property) => {
      switch(property) {
        case 'perfilClave': return item.perfilClave;
        case 'nombrePerfilClave': return item.nombrePerfilClave;
        case 'nombreBackup': return item.nombreBackup;
        case 'planContingencia': return item.planContingencia;
        case 'backupActividades': return item.backupActividades;
        case 'backupHerramientas': return item.backupHerramientas;
        case 'pclaveReunionSeg': return item.pclaveReunionSeg;
        case 'comunicacionCliEquipo': return item.comunicacionCliEquipo;
        case 'comentarios': return item.comentarios;
        default: return item[property];
      }
    };
    this.dataSource.sort = this.sort;
  }


  reloadDataSource() {
    this.dataSource = new MatTableDataSource(this.planCont.detailKeysResourcesList);
    this.dataSource.paginator = this.paginator;
    this.paginator._intl.itemsPerPageLabel = 'Registros por página.'
  }


  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  upload() {
    const fileUpload = this.fileUpload.nativeElement;
    fileUpload.onchange = () => {
      this.files = [];
      for (let index = 0; index < fileUpload.files.length; index++) {
        const file = fileUpload.files[index];
        this.files.push({ data: file, inProgress: false, progress: 0, name: file.name });
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
    console.log("Loading file: " + file.name);
    const formData = new FormData();
    formData.append('file', file.data);

    file.inProgress = true;

    const reader = new FileReader();
    reader.onloadstart = (e) => {
      console.log("loading file...");
      Swal.fire({
        title: 'Leyendo archivo',
        text: 'La operación puede tomar un tiempo',
        allowEscapeKey: false,
        allowOutsideClick: false,
        showConfirmButton: false,
      });
    }

    reader.onload = (e) => {
      console.log("file loaded");
      this.contDocument.contentBase64 = reader.result.toString();
      this.contDocument.projectCode = this.planCont.projectCode;
      this.contDocument.name = file.name;
      Swal.fire({
        icon: 'success',
        title: '¡Archivo Leido!</br> ¿Desea guardar inmediatamente?',
        confirmButtonText: "Guardar",
        showCancelButton: true
      }).then((result) => {
        Swal.fire({
          title: 'Subiendo archivo',
          text: 'La operación puede tomar un tiempo',
          allowEscapeKey: false,
          allowOutsideClick: false,
          showConfirmButton: false,
          onOpen: () => {
            Swal.showLoading();
          }
        });
        if (result.value) {
          console.log("subiendo documento");
          this._planContService.loadDocument(this.contDocument.projectCode, this.contDocument).subscribe(
            (res) => {
              //this.planCont = res;
              Swal.fire({
                icon: 'success',
                title: '¡Archivo cargado!',
                showConfirmButton: true,

              })
            }, (err) => {
              Swal.fire({
                icon: 'error',
                title: '',
                text: 'Ha ocurrido un error'
              });
            });
        }
      })
    };
    reader.readAsDataURL(file.data);
  }


  handleFileSelect(evt) {
    var files = evt.target.files;
    var file = files[0];
    switch (files[0].type) {
      case 'application/vnd.openxmlformats-officedocument.wordprocessingml.document':
        if (files && file) {
          var reader = new FileReader();
          reader.onload = this._handleReaderLoaded_word.bind(this);
          reader.readAsBinaryString(file);
        }
        break;
      case 'image/png':
        if (files && file) {
          var reader = new FileReader();
          reader.onload = this._handleReaderLoaded_img.bind(this);
          reader.readAsBinaryString(file);
        }
        break;
      case 'image/jpeg':
        if (files && file) {
          var reader = new FileReader();
          reader.onload = this._handleReaderLoaded_img.bind(this);
          reader.readAsBinaryString(file);
        }
        break;
      case 'application/pdf':
        if (files && file) {
          var reader = new FileReader();
          reader.onload = this._handleReaderLoaded_pdf.bind(this);
          reader.readAsBinaryString(file);
        }
        break;
      case 'application/vnd.openxmlformats-officedocument.presentationml.presentation':
        if (files && file) {
          var reader = new FileReader();
          reader.onload = this._handleReaderLoaded_ppt.bind(this);
          reader.readAsBinaryString(file);
        }
        break;
      case 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet':
        if (files && file) {
          var reader = new FileReader();
          reader.onload = this._handleReaderLoaded_excel.bind(this);
          reader.readAsBinaryString(file);
        }
        break;
    }
  }
  _handleReaderLoaded_word(readerEvt, tipoMIME) {
    var binaryString = readerEvt.target.result;
    this.base64textString = btoa(binaryString);
    this.base64 = "data:application/msword;base64," + this.base64textString;
  }
  _handleReaderLoaded_img(readerEvt, tipoMIME) {
    var binaryString = readerEvt.target.result;
    this.base64textString = btoa(binaryString);
    this.base64 = "data:image/jpeg;base64," + this.base64textString;
  }
  _handleReaderLoaded_pdf(readerEvt, tipoMIME) {
    var binaryString = readerEvt.target.result;
    this.base64textString = btoa(binaryString);
    this.base64 = "data:application/pdf;base64," + this.base64textString;
  }
  _handleReaderLoaded_ppt(readerEvt, tipoMIME) {
    var binaryString = readerEvt.target.result;
    this.base64textString = btoa(binaryString);
    this.base64 = "data:application/vnd.ms-powerpoint;base64," + this.base64textString;
  }
  _handleReaderLoaded_excel(readerEvt, tipoMIME) {
    var binaryString = readerEvt.target.result;
    this.base64textString = btoa(binaryString);
    this.base64 = "data:application/vnd.ms-excel;base64," + this.base64textString;
  }

 deleteDetail(detail:DetPlanCont ){
    const objIndex = this.planCont.detailKeysResourcesList.findIndex( (obj:DetPlanCont) => {return obj == detail});
    let deleted=null;
    if (objIndex > -1) {
      deleted=this.planCont.detailKeysResourcesList.splice(objIndex, 1);
    }
   if (deleted != null)
     {
      deleted.forEach(detail => {
        this.toDelete.push(detail);
      });
    }

    this.loadPaginator(this.planCont.detailKeysResourcesList);
  }

  agregarPerfil(panel:MatExpansionPanel) {
    if(this.isValidDetail(this.newDetail)==false){
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

    this.planCont.detailKeysResourcesList.push(this.newDetail);
    this.clean();
    this.loadPaginator(this.planCont.detailKeysResourcesList);
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

}
