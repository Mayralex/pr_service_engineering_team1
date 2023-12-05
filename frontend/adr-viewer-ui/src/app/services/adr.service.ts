import {Injectable} from '@angular/core';
import {ADR} from "../interfaces/adr";
import {Observable, of, timer} from "rxjs";
import {HttpClient, HttpParams} from "@angular/common/http";
import {catchError, tap, switchMap, takeWhile} from 'rxjs/operators';


/**
 * Service to communicate with backend endpoints
 */
@Injectable({
  providedIn: 'root'
})
export class AdrService {
  // Backend endpoint information
  private url = 'http://localhost:8080/api/v2';
  private adrByIdUrl = 'getADR';
  private allADRsUrl = 'getAllADRs';

  // Polling variables used in getAllADRs
  private pollingInterval = 3000;
  private continuePolling = true;
  private expNumOfADRs = 0;
  private currentNumOfADRs = -1;

  constructor(
    private http: HttpClient
  ) {
  }

  /** GET all ADRs for the specified repository
   *
   * @param repoOwner - owner of the repository
   * @param repoName - name of the repository
   * @param directoryPath - path to directory where ADRs are stored
   * @param branch - branch of the repository
   *
   * @returns an array of the ADRs in the repository - empty if repository does not exist or an error occurs while fetching.
   */
  getAllADRs(repoOwner: string, repoName: string, directoryPath: string, branch: string): Observable<ADR[]> {
    const requestUrl = `${this.url}/${this.allADRsUrl}`;

    let queryParams = new HttpParams();
    queryParams = queryParams.append("repoOwner", repoOwner);
    queryParams = queryParams.append("repoName", repoName);
    queryParams = queryParams.append("directoryPath", directoryPath);
    queryParams = queryParams.append("branch", branch);

    this.continuePolling = true; // needed to load ADRs again after component is destroyed
    return timer(0, this.pollingInterval).pipe(
      takeWhile(() => this.continuePolling),
      switchMap(() => {
        return this.http.get<ADR[]>(requestUrl, {params: queryParams})
          .pipe(
            tap(_ => this.checkFetchingStatus(_)),
            catchError(this.handleError<ADR[]>('getAllADRs', []))
          );
      })
    );
  }

  /** GET a single ADR by its ID
   *
   * @param id - ID of the ADR
   *
   * @returns the requested ADR by ID - empty if ADR with given ID does not exist or an error occurs while fetching.
   */
  getAdrById(id: number): Observable<ADR> {
    const requestUrl = `${this.url}/${this.adrByIdUrl}`;
    let queryParams = new HttpParams();
    queryParams = queryParams.append("id", id);

    return this.http.get<ADR>(requestUrl, {params: queryParams})
      .pipe(
        tap(_ => console.log(`Fetched ADR by ID =`, id)),
        catchError(this.handleError<ADR>(`getAdrById id = ${id}`))
      );
  }

  /** Checks if the service finished fetching all ADRs from endpoint
   *
   * @returns true if fetching has finished - false otherwise.
   */
  finishedFetchingADRs(): boolean {
    return !this.continuePolling;
  }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// HELPER FUNCTIONS AND ERROR HANDLING ///////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Only for getAllADRs!
   * Stops polling for new ADRs
   */
  private stopPolling(): void {
    this.continuePolling = false;
  }

  /** Only for getAllADRs!
   * Checks if all ADRs are already loaded. If yes, polling is stopped. If not, polling continues
   * @param adrs the currently fetched ADR array
   */
  private checkFetchingStatus(adrs: ADR[]): void {
    if (adrs.length > this.expNumOfADRs) {
      this.expNumOfADRs = adrs.length;
      console.log('Expected number of ADRs:', adrs.length);
    } else if (this.currentNumOfADRs == this.expNumOfADRs) {
      this.stopPolling();

      // Not necessary now, but maybe again if another repository should be fetched in the same session!
      //this.currentNumOfADRs = -1;
      //this.expNumOfADRs = 0;
      console.log('ADRs fully loaded.')
    } else {
      this.currentNumOfADRs = adrs.length;
      console.log('Currently loaded', adrs.length, 'ADRs. Expected:', this.expNumOfADRs);
    }

  }

  /**
   * Handle Http operation that failed.
   * Stop polling.
   * Let the app continue.
   *
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.log(`${operation} failed: ${error.message}`);
      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

}
