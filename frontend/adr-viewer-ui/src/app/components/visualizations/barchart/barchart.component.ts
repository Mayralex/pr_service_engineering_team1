import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ChartConfiguration} from "chart.js";
import {BaseChartDirective} from "ng2-charts";

@Component({
  selector: 'app-barchart',
  templateUrl: './barchart.component.html',
  styleUrls: ['./barchart.component.css']
})
export class BarchartComponent implements OnInit {
  @Input() adrLabels: string[] = [];
  @Input() adrArtifactNumbers: number[] = [];
  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  constructor() {}

  ngOnInit(): void {
    this.updateChartData();
  }

  public barChartLegend = true;
  public barChartPlugins = [];

  public barChartData: ChartConfiguration<'bar'>['data'] = {
    labels: this.adrLabels,
    datasets: [
      {
        data: this.adrArtifactNumbers,
        label: 'Architecture Decision Record',
        backgroundColor: ['rgba(255, 0, 0, 0.6)'],
        borderColor: ['rgba(255, 0, 0, 0.8)'],
        borderWidth: 1
      },
    ],
  };

  updateChartData(): void {
    this.barChartData = {
      labels: this.adrLabels,
      datasets: [
        {
          data: this.adrArtifactNumbers,
          label: 'Architecture Decision Record',
          backgroundColor: ['rgba(255, 0, 0, 0.6)'],
          borderColor: ['rgba(255, 0, 0, 0.8)'],
          borderWidth: 1
        },
      ],
    };
    if (this.chart) {
      this.chart.update();
    }
  }

  public barChartOptions: ChartConfiguration<'bar'>['options'] = {
    scales: {
      x: {
        title: {
          display: true,
          text: 'ADR Title'
        },
        ticks: {
          autoSkip: false,
          maxRotation: 90,
          minRotation: 90,
        }
      },
      y: {
        title: {
          display: true,
          text: 'Number of Artifacts'
        }
      }
    }
  };
}
