import {Component, OnInit} from '@angular/core';
import {ADR} from "../../interfaces/adr";
import {AdrService} from "../../services/adr.service";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})

export class DashboardComponent implements OnInit {
  selectedVisualization: 'bar' | 'line' = 'line';
  adrLabels: string[] = [];
  adrArtifacts: string[] = [];
  adrArtifactNumbers: number[] = [];

  adrsLoaded: boolean = false;
  adrs: ADR[] = [];

  userData: { repoOwner: string; repoName: string; directoryPath: string; branch: string };

  constructor(private adrService: AdrService) {}

  ngOnInit(): void {
    this.loadAdrs();
    this.adrLabels = ['ADR 001', 'ADR 002', 'ADR 003', 'ADR 004', 'ADR 005', 'ADR 006', 'ADR 007', 'ADR 008', 'PLACEHOLDER TITLE', 'ADR 010', 'ADR 011', 'ADR 012', 'ADR 013', 'ADR 014', 'ADR 015', 'ADR 016', 'ADR 017', 'ADR 018', 'ADR 019', 'ADR 020', 'ADR 021', 'ADR 022', 'ADR 023', 'ADR 024', 'ADR 025', 'ADR 026', 'ADR 027']
    this.userData = { repoOwner: '', repoName: '' , directoryPath: '' , branch: '' };
  }

  //TODO: get user data from URL, get ADRs based on project
  loadAdrs() {
    this.adrArtifactNumbers = [];
    this.adrLabels = [];
    this.adrService.getAllADRs('flohuemer', 'graal', 'wasm/docs/arch', 'adrs')
      .subscribe(adrs => {
        if (adrs && adrs.length > 0) {
          this.adrs = adrs;
          for (var adr of adrs) {
            if (adr.title != null && adr.artifacts != null) {
              const splitTitle = adr.title.split(':');
              const label = splitTitle[0];
              if (!this.adrLabels.includes(label)) {
                this.adrLabels.push(label);
              }
              this.adrArtifactNumbers.push(adr.artifacts.length);
            }
          }
          this.adrsLoaded = true;
        } else {
          this.adrsLoaded = false;
        }
      });
  }
}
