import {Component, OnInit, OnDestroy, HostListener} from '@angular/core';
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
  importTaskId: number = -1;

  // Booleans for which HTML content to show
  showADRs = false;
  showEmpty = false;
  isLoading = true;

  // Subscription to ADR service
  private adrSubscription: Subscription;

  // variable for fetched ADRs
  adrs = [] as ADR[];

  // for search by title
  _searchText: string = '';
  get searchText(): string {
    return this._searchText;
  }
  set searchText(value: string) {
    this._searchText = value;
    this.loadPage(this.searchText, this.pageOffset, this.limit);
  }

  //pagination
  limit: number = 8;
  pageOffset: number = 0;
  pageCount: number = 1;

  constructor(
    private adrService: AdrService,
    private route: ActivatedRoute,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    console.log('Listview component was created.');
    this.route.queryParams.subscribe(params => {
      this.importTaskId = params['importTaskId'];
    });
    this.loadPage(this.searchText,parseInt(window.location.hash.substring(1)) || 0, this.limit);
  }

  /**
   * allows the user to set the pageOffset from the URL
   * @param e
   */
  @HostListener('window:hashchange', ['$event'])
  onHashChange(e: HashChangeEvent) {
    const hashvalue = window.location.hash;
    const pageOffsetFromUrlHash = parseInt(hashvalue.replace('#',''))
    if (!isNaN(pageOffsetFromUrlHash)) {
      this.loadPage(this.searchText, pageOffsetFromUrlHash, this.limit);
    }
  }

  onPage(pageNumber: number) : void {
    this.loadPage(this.searchText,pageNumber, this.limit);
  }

  /**
   * Routes to detailview for the given ADR
   * @param adr
   */
  onSelect(adr: ADR): void {
    this.router.navigate(['/detailview'], {
      queryParams: {
        id: adr.id,
        importTaskId: this.importTaskId
      }
    });
  }

  /**
   * Loads a oage of ADRs
   * @param searchText to filter the ADRs
   * @param pageOffset current page
   * @param limit limit of ADRs to display per page
   * @private
   */
  public loadPage(searchText: string, pageOffset: number, limit: number): void {
    window.location.hash = pageOffset.toString();

    this.adrSubscription = this.adrService.getAdrs(this.importTaskId, searchText, pageOffset, limit)
      .subscribe({
        next: page => {
          if (page && page.data.length > 0) {
            this.adrs = page.data;
            this.pageOffset = page.paginationInfo.pageOffset;
            this.pageCount = page.paginationInfo.pageCount;
            this.isLoading = false;
            this.showADRs = true;
          } else {
            this.isLoading = false;
            this.showEmpty = true;
          }
        },
        error: (error) => {
          console.log('Get Next Adrs failed:', error);
        }
      });
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
