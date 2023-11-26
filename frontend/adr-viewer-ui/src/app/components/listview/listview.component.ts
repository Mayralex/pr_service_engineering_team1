import { Component, OnInit } from '@angular/core';
import {ADR} from "../../interfaces/adr";
import {AdrService} from "../../services/adr.service";
import {MessageService} from "../../services/message.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-listview',
  templateUrl: './listview.component.html',
  styleUrls: ['./listview.component.css']
})
export class ListviewComponent implements OnInit {

  userData: { repoOwner: string; repoName: string; directoryPath: string; branch: string };

  showADRs = false;
  showEmpty = false;
  isLoading = true;

  adrs = [] as ADR[];

  constructor(
    private adrService: AdrService,
    private messageService: MessageService,
    private route: ActivatedRoute,
    private router: Router
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
    this.router.navigate(['/detailview'], {
      queryParams:{
        id: adr.id,
      }
    });
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

  isActive(status: string): boolean {
    return status === 'Active'
  }

}
