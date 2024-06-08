import { Component, OnInit, ViewChild, Input, AfterViewInit, QueryList, ViewChildren } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { RealEndDate } from 'src/app/models/RealEndDate';
import { ExcelExportService } from 'src/app/services/excelExport.service';
import { ActivatedRoute, Router } from '@angular/router';
import { formatDate } from '@angular/common';
import { ProjectTrackingService } from 'src/app/services/projectTracking.service';
import { ProjectTracking } from 'src/app/models/ProjectTracking';

@Component({
  selector: 'app-change-log',
  templateUrl: './change-log.component.html',
  styleUrls: ['./change-log.component.scss']
})
export class ChangeLogComponent implements OnInit {

    @Input() projectTracking:ProjectTracking; 
    public paginatorSize: number = 10;

    //TABLA
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort, { static: true }) sort: MatSort;

    displayedColumns: string[] = ['modificationDate', 'username', 'realEndDateInitial', 'realEndDateModified', 'extAgainstIncome', 'extensionReason'];
    dataSource: MatTableDataSource<RealEndDate>;
    endDateLogs: RealEndDate[];

  constructor(private projectTrackingService: ProjectTrackingService, private excelProjectLog: ExcelExportService, private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit(): void {
    if (localStorage.getItem("pt_changes_pageSize") != null)
      this.paginatorSize = parseInt(localStorage.getItem("pt_changes_pageSize"));
    this.loadChanges();
  }
  
  reload(){
    this.loadChanges();
  }

  loadChanges() {
    console.log("LoadChanges");
    const project = this.route.snapshot.paramMap.get('project');

    if (project != null) {
      this.projectTrackingService.getChangeLogs(project).subscribe(
        (response) => {
          this.endDateLogs = response;
          if(this.endDateLogs!=null)
            this.loadPaginator(this.endDateLogs);
        }, (error) => {
          console.log(error);
        }
      );
    }
  }

  handlePaginatorEvent(event: any) {
    this.paginatorSize = event.pageSize;
    localStorage.setItem("pt_changes_pageSize", this.paginatorSize + "");
  }

  loadPaginator(data: Array<RealEndDate>) {
    console.log("loadPaginator");
    this.dataSource = new MatTableDataSource(data);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;    
    this.paginator._intl.itemsPerPageLabel = 'Registros por página.'
    /*this.dataSource.sortingDataAccessor = (item, property) => {
      switch(property) {
        case 'alarm': return item.alarm.level;
        default: return item[property];
      }
    };*/
    
  }

  exportAsXLSX() {
    let dataExport: any[] = [];
    this.endDateLogs.forEach(ptItem => {
      dataExport.push(this.createCellExport(ptItem));
    });
    this.excelProjectLog.exportAsExcelFile(dataExport, 'Logs de Cambios');
  }

  getTypeExtension(extensionType: boolean): string {
    let value = "No Aplica";

    if (extensionType == null) return value;
    if (extensionType) return "Extension contra Ingresos";
    if (extensionType == false) return "Extension contra proyecto";

    return value;
  }

  createCellExport(ptItem: RealEndDate) {
    return {
      'Codigo Proyecto': this.projectTracking.projectCode,
      'Nombre Proyecto': this.projectTracking.projectName,
      'Fecha modificación': formatDate(ptItem.modificationDate, 'dd/MM/yyyy', 'en_US'),
      'Usuario': ptItem.username,
      'Fecha Real Fin - Inicial': formatDate(ptItem.realEndDateInitial, 'dd/MM/yyyy', 'en_US'),
      'Fecha Real Fin - Modificada': formatDate(ptItem.realEndDateModified, 'dd/MM/yyyy', 'en_US'),
      'Tipo Extensión': this.getTypeExtension(ptItem.extAgainstIncome),
      'Comentario': ptItem.extensionReason
    };
  }

}
