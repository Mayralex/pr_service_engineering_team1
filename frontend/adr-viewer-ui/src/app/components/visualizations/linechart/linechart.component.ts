import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ChartConfiguration} from "chart.js";
import {BaseChartDirective} from "ng2-charts";

@Component({
  selector: 'app-linechart',
  templateUrl: './linechart.component.html',
  styleUrls: ['./linechart.component.css']
})
export class LinechartComponent implements OnInit {
  @Input() adrLabels: string[] = [];
  @Input() adrArtifactNumbers: number[] = [];
  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  public lineChartData: ChartConfiguration<'line'>['data'] = {
    labels: ['ADR 1', 'ADR 2', 'ADR 3'],
    datasets: [
      {
        data: [5, 6, 2],
        label: 'Artifacts',
        pointBackgroundColor: ['rgba(255, 0, 0, 0.6)'],
        pointBorderColor: ['rgba(255, 0, 0, 0.8)'],
        borderWidth: 1,
      },
    ],
  };
  constructor() { }

  ngOnInit(): void {
    this.updateChartData();
  }

  updateChartData(): void {
    this.lineChartData = {
      labels: this.adrLabels,
      datasets: [
        {
          data: this.adrArtifactNumbers,
          label: 'Artifacts',
          pointBackgroundColor: ['rgba(255, 0, 0, 0.6)'],
          pointBorderColor: ['rgba(255, 0, 0, 0.8)'],
          borderWidth: 1
        },
      ],
    };
    if (this.chart) {
      this.chart.update();
    }
  }

  public lineChartOptions: ChartConfiguration<'line'>['options'] = {
    elements: {
      line: {
        tension: 0.5
      }
    },
    layout: {
      padding: {
        top: 20,
        left: 80,
        right: 80
      }
    },
    scales: {
      x: {},
      'y-axis-0':
        {
          position: 'left',
        },
    },
  };
}
