import {Component, OnInit} from '@angular/core';
import {ADR} from "../../interfaces/adr";
import {AdrService} from "../../services/adr.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})

export class DashboardComponent implements OnInit {
  selectedVisualization: 'bar' | 'line' | 'graph'| 'pie' | 'lifecycle' | 'timeline' = 'bar';

  adrsLoaded: boolean = false;
  adrs: ADR[] = [];
  importTaskId: number = 0;

  constructor(private adrService: AdrService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.importTaskId = params['importTaskId'];
    });
    this.loadAdrs();
  }

  loadAdrs() {
    this.adrService.getAllADRsOfProject(this.importTaskId)
      .subscribe(adrs => {
        if (adrs && adrs.length > 0) {
          this.adrs = adrs;
          this.adrsLoaded = true;
        } else {
          this.adrsLoaded = false;
        }
      });
  }
}
