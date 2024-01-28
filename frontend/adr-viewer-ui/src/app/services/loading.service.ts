import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs";

/**
 * Service contains information about the fetching status of ADRs for a given repository
 */
@Injectable({
  providedIn: 'root'
})
export class LoadingService {
  public isLoading$ = new BehaviorSubject<boolean>(false);

  constructor() { }

  public toggleLoadingStatusTrue(){
    this.isLoading$.next(true);
  }

  public toggleLoadingStatusFalse(){
    this.isLoading$.next(false);
  }

}
