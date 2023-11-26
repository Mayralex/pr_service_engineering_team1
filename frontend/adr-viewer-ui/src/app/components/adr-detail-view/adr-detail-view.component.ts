import { Component, OnInit } from '@angular/core';
import {ADR} from "../../interfaces/adr";
import {AdrService} from "../../services/adr.service";
import {MessageService} from "../../services/message.service";
import {ActivatedRoute} from "@angular/router";

/**
 * Copied component from old listview - can be adapted to show only one ADR by id!!!!!
 */
@Component({
  selector: 'app-adr-detail-view',
  templateUrl: './adr-detail-view.component.html',
  styleUrls: ['./adr-detail-view.component.css']
})
export class AdrDetailViewComponent implements OnInit {

  userData: { repoOwner: string; repoName: string; directoryPath: string; branch: string };

  showADRs = false;
  showEmpty = false;
  isLoading = true;

  selectedADR?: ADR;
  adrs = [] as ADR[];
  adrById = {} as ADR;

  constructor(
    private adrService: AdrService,
    private messageService: MessageService,
    private route: ActivatedRoute,
  ) { }

  ngOnInit(): void {
    this.userData = { repoOwner: '', repoName: '' , directoryPath: '' , branch: '' };
    this.route.queryParams.subscribe(params => {
      this.userData.repoOwner = params['repoOwner'];
      this.userData.repoName = params['repoName'];
      this.userData.directoryPath = params['directoryPath'];
      this.userData.branch = params['branch'];
    });
    this.getAllADRs(this.userData.repoOwner, this.userData.repoName, this.userData.directoryPath, this.userData.branch);
  }

  onSelect(adr: ADR): void {
    this.selectedADR = adr;
    this.messageService.add(`ADRComponent: Selected adr id=${adr.id}`);
  }

  getAllADRs(repoOwner: string, repoName: string, directoryPath: string, branch: string): void {
    this.adrService.getAllADRs(repoOwner, repoName, directoryPath, branch)
      .subscribe(adrs => {
        if(adrs && adrs.length > 0){
          this.adrs = adrs;
          this.isLoading = false;
          this.showADRs = true;
        } else{
          this.isLoading = false;
          this.showEmpty = true;
        }
      });
  }

  getAdrById(id: number): void {
    this.adrService.getAdrById(id)
      .subscribe(adr => this.adrById = adr);
  }
}
