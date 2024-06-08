import { Component, OnInit, Input } from '@angular/core';
import { ChartDataSets, ChartOptions, ChartType } from 'chart.js';
import { Label } from 'ng2-charts';


@Component({
  selector: 'app-graphi-bar-chart',
  templateUrl: './graphi-bar-chart.component.html',
  styleUrls: ['./graphi-bar-chart.component.scss']
})
export class GraphiBarChartComponent implements OnInit {
  /**Mostar el procentaje de desvio y fecha de modificación  */
  @Input() data: ChartDataSets[] = [];
  @Input() labels: Label[] = [];

  public barChartOptions: ChartOptions = {

    responsive: true,
  };

  //etiquetas del eje x
  public barChartType: ChartType = 'bar';
  public barChartLegend = true;
  public barChartPlugins = [];

  /**
   * aun se cargar los datos por defecto.
   */
  // events
  chartClicked({ event, active }: { event: MouseEvent, active: {}[] }): void {
    //console.log(event, active);
  }

  chartHovered({ event, active }: { event: MouseEvent, active: {}[] }): void {
    // console.log(event, active);
  }

  constructor() { }
  //al momento de iniciar cargamos data temporal si el gráfico no tiene datos.
  datoTempora1() {
    this.data = [{ data: [5], label: 'temp' }];
  }
  ngOnInit(): void {
    //al momento de iniciar cargamos data temporal
    this.datoTempora1();
  }

}
