import { Component, OnInit, Input } from '@angular/core';
import { ProjectTracking } from 'src/app/models/ProjectTracking';
import { Label } from 'ng2-charts';
import { ChartDataSets } from 'chart.js';
import { ProjectHistory } from 'src/app/models/ProjectHistory';
import { DatePipe } from '@angular/common';
import { ProjectTrackingService } from 'src/app/services/projectTracking.service';
import { Validators } from '@angular/forms';

@Component({
  selector: 'app-history-tab',
  templateUrl: './history-tab.component.html',
  styleUrls: ['./history-tab.component.scss']
})
export class HistoryTabComponent implements OnInit {
  constructor(private projectTrackingService: ProjectTrackingService) { }

  /**
  * clave: bF8G1F7M2t 
  * EXT-016147-00015
  * c
  */
  labels: Label[] = [];
  data: ChartDataSets[] = [];

  ///Gráficos tecnicos
  labelsTechn: Label[] = [];
  dataTechn: ChartDataSets[] = [];

  /**** Gráficos Service*/
  labelsService: Label[] = [];
  dataService: ChartDataSets[] = [];

  /**** Gráficos Agil*/
  labelsAgil: Label[] = [];
  dataAgil: ChartDataSets[] = [];

  /**** Gráficos Staff*/
  labelsStaff: Label[] = [];
  dataStaff: ChartDataSets[] = [];


  projectTracking: ProjectTracking = new ProjectTracking();
  datepipe: DatePipe = new DatePipe("es-CL");
  projectHistory: ProjectHistory[];

  iterator = [1, 2, 3, 4, 5];

  ngOnInit(): void {
  }


  loadData(projectTracking: ProjectTracking) {
    this.projectTracking = projectTracking;
    this.projectTrackingService.getHistory(projectTracking.projectCode).subscribe(
      (resp) => {
        this.projectHistory = resp;
        this.prepareGraph();
        console.log(resp);
      },
      (error) => {
        console.log(error);
      }
    );
  }

  prepareGraph() {
    this.labels = [];
    this.data = [];
    /**** Gráficos tecnicos */
    this.labelsTechn = [];
    this.dataTechn = [];

    /**** Gráficos Service */
    this.labelsService = [];
    this.dataService = [];

    /**** Gráficos Agil*/
    this.labelsAgil = [];
    this.dataAgil = [];

    /**** Gráficos Staff*/
    this.labelsStaff = [];
    this.dataStaff = [];

    if (this.projectHistory == null) return;

    if ("STAFF AUGMENTATION" === this.projectTracking.serviceLine) {
      ///text = "Staff Augmentation";
       //Arreglos para las variables del eje y
       let staffValues=new Map();

       this.projectHistory.forEach(history => {
         if (history.staff != null) {
           //ejeX
           console.log(history.lastModified);
           this.labelsStaff.push(this.datepipe.transform(history.lastModified,"medium"));
           //ejeY

           history.staff.detailProjectProfileList.forEach(profile=>{
            let profileValues=staffValues.get(profile.profileName);
            if(profileValues==null)
              profileValues=[];

            profileValues.push(profile.tarifa);
            staffValues.set(profile.profileName,profileValues);
           });
          }
       });
       //Barras 
       staffValues.forEach((value: any, key: any)=>{
         this.dataStaff.push({data:value,label:key});
       });
    }
    else if (1 === this.projectTracking.projectTypeCode) {
      //text = "Servicio"; EXT-003002-00236
      let percentageEstDeliveredVal = [];
      let percentageReqResolvedVal = [];

      this.projectHistory.forEach(history => {
        if (history.service != null) {
          //ejeX
          this.labelsService.push(this.datepipe.transform(history.lastModified));
          //ejeY
          let percentageEstDelivered = history.service.percentageEstDelivered;
          let percentageReqResolved = history.service.percentageReqResolved;

          //Datos 
          percentageEstDeliveredVal.push(percentageEstDelivered);
          percentageReqResolvedVal.push(percentageReqResolved);
        }
      });
      //Barras 
      this.dataService.push({ data: percentageEstDeliveredVal, label: '% Estimaciones entregadas en plazo' });
      this.dataService.push({ data: percentageReqResolvedVal, label: '% Requierimentos entregadas en plazo' });
    }

    else if (2 === this.projectTracking.projectTypeCode) {
      /// text = "Agil";
      //

    }

    //
    if (3 === this.projectTracking.projectTypeCode)//proyecto tradicional
    {
      let values = [];
      this.projectHistory.forEach(history => {
        this.labels.push(this.datepipe.transform(history.lastModified));
        let deviation = history.traditional.deviationPercentage;
        if (deviation == null) deviation = 0;
        values.push(deviation);
      });
      this.data.push({ data: values, label: '% Desvio' });

    }

    /***
      * Gráficos Técnicos
      * select  from project_history where project_code ='EXT-016147-00015'
      * select from project_history where project_code ='EXT-003004-00034'
      * 
      * select from project_tracking where project_code ='EXT-016147-00015'
      * select from project_tracking where project_code ='EXT-003004-00034'
      */

    {

      //Arreglos para las variables del eje y
      let testCaseVal = [];
      let prodErrorVal = [];
      let numrollVal = [];
      let periodQualVal = [];

      this.projectHistory.forEach(history => {
        if (history.technical != null) {
          //ejeX
          this.labelsTechn.push(this.datepipe.transform(history.lastModified));
          //ejeY
          let testCasesIndex = history.technical.testCasesIndex;
          let prodErrorIndex = history.technical.prodErrorIndex;
          let numRollbakReqPer = history.technical.numRollbakReqPer;
          let periodQualityComp = history.technical.periodQualityComp;

          //Datos 
          testCaseVal.push(testCasesIndex);
          prodErrorVal.push(periodQualityComp);
          numrollVal.push(numRollbakReqPer);
          periodQualVal.push(prodErrorIndex);

        }
      });
      //Barras 
      this.dataTechn.push({ data: testCaseVal, label: 'Casos de Prueba' });
      this.dataTechn.push({ data: prodErrorVal, label: 'Errores de Producción' });
      this.dataTechn.push({ data: numrollVal, label: 'RollBack' });
      this.dataTechn.push({ data: periodQualVal, label: 'Calidad de Código' });
    }
    {

    }
    console.log(this.labels);
    console.log(this.data);
  }

  //Agregamos el nombre del Indicador
  public graphiName: string = 'Indicadores Técnicos';

  public STAFF_TEXT: string = "Staff Augmentation";
  public SERVICE_TEXT: string = "Servicio";
  public AGILE_TEXT: string = "Ágil";
  public TRADITIONAL_TEXT: string = "Tradicional";

  getProjectTypeText(): string {
    let text = "";
    //1 servicio
    //2 agile
    //3 tradicional
    if ("STAFF AUGMENTATION" === this.projectTracking.serviceLine)
      text = this.STAFF_TEXT;
    else if (1 === this.projectTracking.projectTypeCode)
      text = this.SERVICE_TEXT;
    else if (2 === this.projectTracking.projectTypeCode)
      text = this.AGILE_TEXT;
    else if (3 === this.projectTracking.projectTypeCode)
      text = this.TRADITIONAL_TEXT;
    return text;
  }

}
