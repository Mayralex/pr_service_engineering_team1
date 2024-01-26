import { Component, OnInit } from '@angular/core';
import {ADR} from "../../interfaces/adr";
import {AdrService} from "../../services/adr.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-adr-detail-view',
  templateUrl: './adr-detail-view.component.html',
  styleUrls: ['./adr-detail-view.component.css']
})
export class AdrDetailViewComponent implements OnInit {
// id of the selected ADR
  id: number;
  // id of the current import task
  importTaskId: number;
  // the selected ADR
  adrById?: ADR = new ADR();
  // all ADRs of the project
  adrs: ADR[] = [];

  constructor(
    private adrService: AdrService,
    private route: ActivatedRoute,
    private router: Router,
  ) { }

  /**
   * initialize the selected ADR
   */
  ngOnInit(): void {
    this.id = -1;
    this.route.queryParams.subscribe(params => {
      this.id = params['id'];
      if(params['importTaskId']) {
        this.importTaskId = params['importTaskId'];
      }
      else {
        // @ts-ignore
        this.importTaskId = +sessionStorage.getItem('importTaskId');
      }
      if (this.id) {
        this.getAdrById(this.id)
      }
    });
    this.getAdrById(this.id);
    this.getAllAdrsOfProject();
  }

  /**
   * get a ADR with the
   * @param id
   */
  getAdrById(id: number): void {
    this.adrService.getAdrById(id)
      .subscribe(adr => this.adrById = adr);
  }
  /**
   * check wether an ADR is active
   * @param status
   */
  isActive(status: string | undefined): boolean {
    return status === 'Active'
  }
  /**
   * navigate to the related ADR
   * @param reference
   */
  navigateToAdr(reference: string): void {
    if (this.adrs && this.adrs.length > 0) {
      const matchedAdr = this.adrs.find(adr => adr.title.includes(reference));

      if (matchedAdr) {
        const matchedAdrId: number = matchedAdr.id;
        this.router.navigate(['/detailview'], {
          queryParams: {
            id: matchedAdrId,
            importTaskId: this.importTaskId
          }
        });
      } else {
        console.log('ADR not found');
        // Handle case where ADR reference is not found
      }
    } else {
      console.log('ADRs not initialized');
      // Handle case where ADRs are not initialized yet
    }
  }

  private getAllAdrsOfProject(): void {
    this.adrService.getAllADRsOfProject(this.importTaskId)
      .subscribe({
        next: adrs => {
          if (adrs && adrs.length > 0) {
            this.adrs = adrs;
          }
        },
        error: (error) => {
          console.log('Get Adrs failed:', error);
        }
      });
  }
}
