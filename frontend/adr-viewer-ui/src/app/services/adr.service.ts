import {Injectable} from '@angular/core';
import {ADR} from "../interfaces/adr";
import {Observable, of, timer} from "rxjs";
import {HttpClient, HttpParams} from "@angular/common/http";
import {catchError, tap, switchMap, takeWhile} from 'rxjs/operators';
import {AdrPage} from "../interfaces/adrPage";
import {ImportTask} from "../interfaces/ImportTask";
import {CommitData} from "../interfaces/commitData";

/**
 * Service to communicate with backend endpoints
 */
@Injectable({
  providedIn: 'root'
})
export class AdrService {
  // Backend endpoint information
  private url = 'http://localhost:8080/api/v2';
  private adrByIdUrl = 'adr';
  private allADRsUrl = 'getAllADRs';
  private pageADRsUrl = 'ADR';
  private allADRsOfProjectUrl = 'getAllADRsOfProject';
  private commitHistoryUrl = 'getHistory'

  // Polling variables used in getAllADRs
  private pollingInterval = 3000;
  private continuePolling = true;
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
  getAllADRs(): Observable<ADR[]> {
    const requestUrl = `${this.url}/${this.allADRsUrl}`;

    this.continuePolling = true; // needed to load ADRs again after component is destroyed
    return timer(0, this.pollingInterval).pipe(
      takeWhile(() => this.continuePolling),
      switchMap(() => {
        return this.http.get<ADR[]>(requestUrl)
          .pipe(
            tap(_ => this.checkFetchingStatus(_)),
            catchError(this.handleError<ADR[]>('getAllADRs', []))
          );
      })
    );
  }

  /**
   * get all ADRs of a project (ADRs have the same importTaskId)
   * @param importTaskId the ID of the importTask the ADR belongs to
   */
  getAllADRsOfProject(importTaskId: number): Observable<ADR[]> {
    const requestUrl = `${this.url}/${this.allADRsOfProjectUrl}`;

    let queryParams = new HttpParams();
    queryParams = queryParams.append("importTaskId", importTaskId);

    return this.http.get<ADR[]>(requestUrl, {params: queryParams});
  }

  /** GET a single ADR by its ID
   *
   * @param id - ID of the ADR
   *
   * @returns the requested ADR by ID - empty if ADR with given ID does not exist or an error occurs while fetching.
   */
  getAdrById(id: number): Observable<ADR> {
    const requestUrl = `${this.url}/${this.adrByIdUrl}/${id}`;

    return this.http.get<ADR>(requestUrl)
      .pipe(
        tap(_ => console.log(`Fetched ADR by ID =`, id)),
        catchError(this.handleError<ADR>(`getAdrById id = ${id}`))
      );
  }



  /**
   * Start analysing a new repository
   *
   * @param repoOwner - owner of the repository
   * @param repoName - name of the repository
   * @param directoryPath - path to directory where ADRs are stored
   * @param branch - branch of the repository
   * @returns the requested ADR by ID - empty if ADR with given ID does not exist or an error occurs while fetching.
   */
  analyseRepository(repoOwner: string, repoName: string, directoryPath: string, branch: string): Observable<ImportTask> {
    const requestUrl = `${this.url}/import_task`;

    let queryParams = new HttpParams();
    queryParams = queryParams.append("repoOwner", repoOwner);
    queryParams = queryParams.append("repoName", repoName);
    queryParams = queryParams.append("directoryPath", directoryPath);
    queryParams = queryParams.append("branch", branch);

    return this.http.post<ImportTask>(requestUrl, {}, {params: queryParams})
      .pipe(
        tap(_ => console.log(`Started analysis`)),
        catchError(this.handleError<ImportTask>(`Failed to start analysis`))
      );
  }

  /**
   * Retrieve an import task based on it's id
   *
   * @param id - ID of the import task
   * @returns the requested import task - empty if ADR with given ID does not exist or an error occurs while fetching.
   */
  getImportTaskById(id: number): Observable<ImportTask> {
    const requestUrl = `${this.url}/import_task/${id}`;

    return this.http.get<ImportTask>(requestUrl)
      .pipe(
        tap(_ => console.log(`Fetched Import Task by ID =`, id)),
        catchError(this.handleError<ImportTask>(`getImportTaskById id = ${id}`))
      );
  }

  /**
   * Retrieve the last analyzed import task
   */
  getLastImportTask(): Observable<ImportTask> {
    const requestUrl = `${this.url}/import_task/last`;

    return this.http.get<ImportTask>(requestUrl)
      .pipe(
        tap(_ => console.log(`Fetched last ImportTask`)),
        catchError(this.handleError<ImportTask>(`getLastImportTask`))
      )
  }

  /** GET all ADRs of a page, filter searchText
   *
   * @param importTaskId - ID of the import task
   * @param searchText - filter ADRs by title
   * @param pageOffset - offset of the current page
   * @param limit - number of ADRs per page
   *
   * @returns a Page of the ADRs
   */
  getAdrs(importTaskId: number, searchText: string, pageOffset: number, limit: number): Observable<AdrPage> {
    let requestUrl = `${this.url}/${this.pageADRsUrl}`;

    let queryParams = new HttpParams();
    queryParams = queryParams.append("importTaskId", importTaskId);
    queryParams = queryParams.append("query", searchText);
    queryParams = queryParams.append("pageOffset", pageOffset);
    queryParams = queryParams.append("limit", limit);

    return this.http.get<AdrPage>(requestUrl, {params: queryParams})
  }


  /**
   * get the commit history of an ADR
   * @param importTaskId to get repository data in the backend for the GraphQl request
   * @param filePath of the file for the commit history
   */
  getCommitData(importTaskId: number, filePath: string): Observable<CommitData> {
    let requestUrl = `${this.url}/${this.commitHistoryUrl}`;

    let queryParams = new HttpParams();
    queryParams = queryParams.append("importTaskId", importTaskId);
    queryParams = queryParams.append("filePath", filePath);

    return this.http.get<CommitData>(requestUrl, {params: queryParams})
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
    let expNumOfADRs = Number(sessionStorage.getItem('expNumOfADRs')) || -1;
    console.log('curr:', this.currentNumOfADRs, 'exp:', expNumOfADRs);
    if (adrs.length > expNumOfADRs) {
      sessionStorage.setItem('expNumOfADRs', JSON.stringify(adrs.length));
      console.log('Expected number of ADRs:', adrs.length);
    } else if (this.currentNumOfADRs == expNumOfADRs) {
      this.stopPolling();

      // Not necessary now, but maybe again if another repository should be fetched in the same session!
      this.currentNumOfADRs = -1;
      //this.expNumOfADRs = 0;
      console.log('ADRs fully loaded.')
    } else {
      this.currentNumOfADRs = adrs.length;
      console.log('Currently loaded', adrs.length, 'ADRs. Expected:', expNumOfADRs);
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
