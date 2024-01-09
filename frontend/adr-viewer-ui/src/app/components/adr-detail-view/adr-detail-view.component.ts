import { Component, OnInit } from '@angular/core';
import {ADR} from "../../interfaces/adr";
import {AdrService} from "../../services/adr.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-adr-detail-view',
  templateUrl: './adr-detail-view.component.html',
  styleUrls: ['./adr-detail-view.component.css']
})
export class AdrDetailViewComponent implements OnInit {

  id: number;
  importTaskId: number;
  adrById:ADR;
  adrs: ADR[];

  private adrSubscription: Subscription;

  constructor(
    private adrService: AdrService,
    private route: ActivatedRoute,
    private router: Router,
  ) { }

  ngOnInit(): void {
    //TODO: Exception handling für z.B. id bleibt -1
    this.id = -1;
    this.getAllAdrs();
    this.route.queryParams.subscribe(params => {
      this.id = params['id'];
      if  (this.id) {
        this.getAdrById(this.id)
      }
    });
    this.getAdrById(this.id);
  }

  getAdrById(id: number): void {
    this.adrService.getAdrById(id)
      .subscribe(adr => this.adrById = adr);
  }

  isActive(status: string): boolean {
    return status === 'Active'
  }

  navigateToAdr(reference: string): void {
    if (this.adrs && this.adrs.length > 0) {
      const matchedAdr = this.adrs.find(adr => adr.title.includes(reference));

      if (matchedAdr) {
        const matchedAdrId: number = matchedAdr.id;
        this.router.navigate(['/detailview'], {
          queryParams: {
            id: matchedAdrId,
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

  private getAllAdrs(): void {
    this.adrSubscription = this.adrService.getAllADRs()
      .subscribe({
        next: adrs => {
          if (adrs && adrs.length > 0) {
            this.adrs = adrs;
          }
        },
        error: (error) => {
          console.log('Get Next Adrs failed:', error);
        }
      });
  }
}
