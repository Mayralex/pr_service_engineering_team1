import {Component, OnInit, OnDestroy} from '@angular/core';
import {ADR} from "../../interfaces/adr";
import {AdrService} from "../../services/adr.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-listview',
  templateUrl: './listview.component.html',
  styleUrls: ['./listview.component.css']
})
export class ListviewComponent implements OnInit, OnDestroy {

  // User input data
  userData: { repoOwner: string; repoName: string; directoryPath: string; branch: string };

  // Booleans for which HTML content to show
  showADRs = false;
  showEmpty = false;
  isLoading = true;

  // Subscription to ADR service
  private adrSubscription: Subscription;

  // variable for fetched ADRs
  adrs = [] as ADR[];

  // for search by status
  searchText: string = '';

  constructor(
    private adrService: AdrService,
    private route: ActivatedRoute,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    console.log('Listview component was created.');
    this.userData = {repoOwner: '', repoName: '', directoryPath: '', branch: ''};
    this.route.queryParams.subscribe(params => {
      this.userData.repoOwner = params['repoOwner'];
      this.userData.repoName = params['repoName'];
      this.userData.directoryPath = params['directoryPath'];
      this.userData.branch = params['branch'];
    });
    this.getAllADRs(this.userData.repoOwner, this.userData.repoName, this.userData.directoryPath, this.userData.branch);
  }

  /**
   * Routes to detailview for the given ADR
   * @param adr
   */
  onSelect(adr: ADR): void {
    this.router.navigate(['/detailview'], {
      queryParams: {
        id: adr.id,
      }
    });
  }

  /**
   * Subscribes to ADR service (adr.service.getAllADRs). Updates the view accordingly.
   * @param repoOwner
   * @param repoName
   * @param directoryPath
   * @param branch
   * @private
   */
  private getAllADRs(repoOwner: string, repoName: string, directoryPath: string, branch: string): void {
    this.adrSubscription = this.adrService.getAllADRs(repoOwner, repoName, directoryPath, branch)
      .subscribe(adrs => {
          if (adrs && adrs.length > 0) {
            this.adrs = adrs;
            this.isLoading = false;
            this.showADRs = true;
          } else {
            this.isLoading = false;
            this.showEmpty = true;
          }
        },
        (error) => {
          console.log('Something went wrong:', error);
        }
      );
  }

  ngOnDestroy(): void {


    console.log('Listview component was destroyed.');
  }

  /**
   * Checks if the given ADR status is active
   * @param status ADR status parameter
   */
  isActive(status: string): boolean {
    return status === 'Active'
  }

}
