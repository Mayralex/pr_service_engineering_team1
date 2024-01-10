import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Chart,ChartConfiguration} from "chart.js";
import {BaseChartDirective} from "ng2-charts";
import ChartDataLabels from 'chartjs-plugin-datalabels';
import {ADR} from "../../../interfaces/adr";

@Component({
  selector: 'app-piechart',
  templateUrl: './piechart.component.html',
  styleUrls: ['./piechart.component.css']
})

export class PiechartComponent implements OnInit {
  @Input() adrs: ADR[] = [];
  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  adrsStatus : string[] = [];
  activeAdrsCount: number = 1;
  activeAdrs: string[] = [];
  deprecatedAdrsCount: number = 1;
  deprecatedAdrs: string[] = [];
  constructor() {
    Chart.register(ChartDataLabels);
    }

  ngOnInit(): void {
    this.getAdrStatus();
    this.getActiveAdrs();
    this.getDeprecatedAdrs();
    this.updateChartData();
  }

  public pieChartLegend = true;
  public pieChartPlugins = [];

  private getAdrStatus() {
    for (var adr of this.adrs) {
        this.adrsStatus.push(adr.status);
      if (adr.status == 'Active'){
        const adrLabel = adr.title.split(':')[0];
        this.activeAdrs.push(adrLabel);
      }
      if (adr.status == 'Deprecated'){
        const adrLabel = adr.title.split(':')[0];
        this.deprecatedAdrs.push(adrLabel);
      }
      }
    }

  private count(array: any[], status: any): number {
    return array.filter(n => n === status).length;
  }

  private getActiveAdrs() {
    console.log("Aktive ADRS-Array",this.activeAdrs);
    this.activeAdrsCount = this.count(this.adrsStatus,'Active');
  }

  private getDeprecatedAdrs() {
    this.deprecatedAdrsCount= this.count(this.adrsStatus,'Deprecated');
  }

  /* --- PIECHART CONFIGURATION --- */
  public pieChartData: ChartConfiguration<'pie'>['data'] = {
    labels:['Active', 'Deprecated'],
    datasets: [
      {
        data: [this.activeAdrsCount,this.deprecatedAdrsCount],
        backgroundColor:[
          'rgb(0, 84, 127)',
          'rgb(0, 127, 191)'],
        hoverBackgroundColor:[
          'rgb(0, 42, 64)',
          'rgb(0, 110, 166)'],
      },
    ],
  }
  ;

  public pieChartOptions: ChartConfiguration<'pie'>['options'] = {
    plugins: {
      datalabels: {
        display: true,
        formatter: (val, ctx) => {
          // Grab the label for this value
          // @ts-ignore
          const label = ctx.chart.data.labels[ctx.dataIndex];
          return `${label}: ${val}`;
        },
        color: '#fff',
        font: {
          size: 30,
        },
      },
      tooltip:{
        callbacks: {
          label: context => {
          if (context.label === 'Active'){
            return this.activeAdrs;
          }
          return this.deprecatedAdrs;
          }
        },
        bodyFont: {
          size:20
        }
      }
    },
    maintainAspectRatio: false
  }

  updateChartData(): void {
    this.pieChartData = {
      labels: ['Active', 'Deprecated'],
      datasets: [
        {
          data: [this.activeAdrsCount,this.deprecatedAdrsCount],
          backgroundColor:[
            'rgb(0, 84, 127)',
            'rgb(0, 127, 191)'],
          hoverBackgroundColor:[
            'rgb(0, 42, 64)',
            'rgb(0, 110, 166)'],
        },
      ],
    };
    if (this.chart) {
      this.chart.update();
    }
  }
}
