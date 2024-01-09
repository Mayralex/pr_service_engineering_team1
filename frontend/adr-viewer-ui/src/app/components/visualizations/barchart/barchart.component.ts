import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ChartConfiguration} from "chart.js";
import {BaseChartDirective} from "ng2-charts";
import {ADR} from "../../../interfaces/adr";

@Component({
  selector: 'app-barchart',
  templateUrl: './barchart.component.html',
  styleUrls: ['./barchart.component.css']
})
export class BarchartComponent implements OnInit {
  @Input() adrs: ADR[] = [];
  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  adrLabels: string[] = [];
  adrArtifactNumbers: number[] = [];

  constructor() {}

  ngOnInit(): void {
    this.getAdrLabels();
    this.getAdrArtifactNumbers();
    this.updateChartData();
  }

  private getAdrLabels() {
    for (var adr of this.adrs) {
      if (adr != null && adr.title) {
        const adrLabel = adr.title.split(':')[0];
        this.adrLabels.push(adrLabel);
      }
    }
  }

  private getAdrArtifactNumbers() {
    for (var adr of this.adrs) {
      if (adr != null &&  Array.isArray(adr.artifacts)) {
        const adrArtifactNumber = adr.artifacts.length;
        this.adrArtifactNumbers.push(adrArtifactNumber);
      } else {
        // current behviour if no Artifacts or Artifacts coul not be parsed
        this.adrArtifactNumbers.push(0);
      }
    }
  }

  /* --- BARCHART CONFIGURATION --- */

  public barChartLegend = true;
  public barChartPlugins = [];

  public barChartData: ChartConfiguration<'bar'>['data'] = {
    labels: this.adrLabels,
    datasets: [
      {
        data: this.adrArtifactNumbers,
        label: 'Artifacts',
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
          label: 'Artifacts',
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
