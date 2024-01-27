import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs";

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
