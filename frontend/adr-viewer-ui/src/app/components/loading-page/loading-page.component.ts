import { Component, OnInit } from '@angular/core';
import {timer} from "rxjs";
import {switchMap, takeWhile} from "rxjs/operators";
import {ActivatedRoute, Router} from "@angular/router";
import {AdrService} from "../../services/adr.service";

@Component({
  selector: 'app-loading-page',
  templateUrl: './loading-page.component.html',
  styleUrls: ['./loading-page.component.css']
})
export class LoadingPageComponent implements OnInit {

  private POLLING_INTERVAL = 1000;
  private importTaskId = 0;
  private importFinished = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private adrService: AdrService
  ) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.importTaskId = params['importTaskId'];
      this.startPolling();
    });
  }

  startPolling(): void {
    timer(0, this.POLLING_INTERVAL)
      .pipe(
        takeWhile(() => !this.importFinished),
        switchMap(() => this.adrService.getImportTaskById(this.importTaskId))
      )
      .subscribe({
        next: (importTask) => {
          this.importFinished = importTask.finished;

          if (importTask.finished) {
            this.router.navigate(['/listview'], {
              queryParams: {
                importTaskId: importTask.id,
                repoOwner: importTask.repoOwner,
                repoName: importTask.repoName,
                directoryPath: importTask.directoryPath,
                branch: importTask.branch,
              }
            });
          }
        },
        error: err => {
          // TODO
        }
      });
  }

}
