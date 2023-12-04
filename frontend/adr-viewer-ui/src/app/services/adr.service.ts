import {Injectable} from '@angular/core';
import {ADR} from "../interfaces/adr";
import {Observable, of, timer} from "rxjs";
import {HttpClient, HttpParams} from "@angular/common/http";
import {catchError, tap, switchMap} from 'rxjs/operators';


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

  // Polling variables
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
   * @returns an array of the ADRs in the repository
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
      switchMap(() => {
        if (!this.continuePolling) {
          throw new Error('Polling stopped.');
        }
        return this.http.get<ADR[]>(requestUrl, {params: queryParams})
          .pipe(
            tap(_ => this.checkFetchingStatus(_)),
            catchError(this.handleError<ADR[]>('getAllADRs', []))
          );
      })
    );
  }

  /**
   * Checks if all ADRs are already loaded. If yes, polling is stopped. If not, polling continues
   * @param adrs the currently fetched ADR array
   */
  private checkFetchingStatus(adrs: ADR[]): void {
    if (adrs.length > this.expNumOfADRs) {
      this.expNumOfADRs = adrs.length;
      console.log('Expected number of ADRs:', adrs.length);
    } else if (this.currentNumOfADRs == this.expNumOfADRs) {
      this.stopPolling();
      this.currentNumOfADRs = -1;
      this.expNumOfADRs = 0;
    } else {
      this.currentNumOfADRs = adrs.length;
      console.log('Currently loaded', adrs.length, 'ADRs. Expected:', this.expNumOfADRs);
    }

  }

  /**
   * Stops polling for new ADRs
   */
  stopPolling(): void {
    this.continuePolling = false;
  }

  /** GET a single ADR by its ID
   *
   * @param id - ID of the ADR
   *
   * @returns the requested ADR by ID
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
      this.stopPolling();
      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

}
