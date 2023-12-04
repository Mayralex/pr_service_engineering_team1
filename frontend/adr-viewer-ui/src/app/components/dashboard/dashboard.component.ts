import {Component, OnInit, ViewChild} from '@angular/core';
import {ADR} from "../../interfaces/adr";
import {ChartConfiguration} from "chart.js";
import {AdrService} from "../../services/adr.service";
import {BaseChartDirective} from "ng2-charts";


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})

export class DashboardComponent implements OnInit {
  adrLabels: string[] = [];
  adrArtifacts: string[] = [];
  adrArtifactNumbers: number[] = [];

  adrsLoaded: boolean = true;
  adrs: ADR[] = [];

  userData: { repoOwner: string; repoName: string; directoryPath: string; branch: string };
  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  constructor(private adrService: AdrService) {}

  ngOnInit(): void {
    this.adrLabels = ['ADR 001', 'ADR 002', 'ADR 003', 'ADR 004', 'ADR 005', 'ADR 006', 'ADR 007', 'ADR 008', 'PLACEHOLDER TITLE', 'ADR 010', 'ADR 011', 'ADR 012', 'ADR 013', 'ADR 014', 'ADR 015', 'ADR 016', 'ADR 017', 'ADR 018', 'ADR 019', 'ADR 020', 'ADR 021', 'ADR 022', 'ADR 023', 'ADR 024', 'ADR 025', 'ADR 026', 'ADR 027']
    this.userData = { repoOwner: '', repoName: '' , directoryPath: '' , branch: '' };
  }

  public barChartLegend = true;
  public barChartPlugins = [];

  public barChartData: ChartConfiguration<'bar'>['data'] = {
    //labels : ['ADR 001', 'ADR 002', 'ADR 003', 'ADR 004', 'ADR 005', 'ADR 006', 'ADR 007', 'ADR 008', 'ADR 009', 'ADR 010', 'ADR 011', 'ADR 012', 'ADR 013', 'ADR 014', 'ADR 015', 'ADR 016', 'ADR 017', 'ADR 018', 'ADR 019', 'ADR 020', 'ADR 021', 'ADR 022', 'ADR 023', 'ADR 024', 'ADR 025', 'ADR 026', 'ADR 027'],
    labels: this.adrLabels,
    datasets: [
      {
        //data: [5, 10, 20, 3, 4, 17, 32, 2, 24, 46, 34, 23, 3, 13, 13, 34, 35, 43, 23, 12, 12, 1, 11, 34, 21, 11, 2 ],
        data: this.adrArtifactNumbers,
        label: 'Architecture Decision Record',
        backgroundColor: ['rgba(255, 0, 0, 0.6)'],
        borderColor: ['rgba(255, 0, 0, 0.8)'],
        borderWidth: 1
      },
    ],
  };

  public barChartOptions: ChartConfiguration<'bar'>['options'] = {
    scales: {
      x: {
        title: {
          display: true,
          text: 'ADR Title'
        },
        ticks: {
          autoSkip: false, // Disable automatic skipping of labels
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

  loadAdrs() {
    this.adrArtifactNumbers.length = 0;
    this.adrLabels.length = 0;
    this.adrService.getAllADRs('flohuemer', 'graal', 'wasm/docs/arch', 'adrs')
      .subscribe(adrs => {
        if (adrs && adrs.length > 0) {
          this.adrs = adrs;
          for (var adr of adrs) {
            if (adr.title != null && adr.artifacts != null) {
              const splitTitle = adr.title.split(':');
              this.adrLabels.push(splitTitle[0]);
              this.adrArtifactNumbers.push(adr.artifacts.length)
            }
          }
          this.adrsLoaded = true;
          this.barChartData.labels = this.adrLabels.slice();
          this.barChartData.datasets[0].data = this.adrArtifactNumbers.slice();
          this.chart?.update();
        } else {
          this.adrsLoaded = false;
        }
      });
  }
}
