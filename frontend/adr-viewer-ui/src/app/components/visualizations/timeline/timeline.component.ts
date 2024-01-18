import {Component, Input, OnInit} from '@angular/core';
import {ADR} from "../../../interfaces/adr";
import {AdrService} from "../../../services/adr.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-timeline',
  templateUrl: './timeline.component.html',
  styleUrls: ['./timeline.component.css']
})
export class TimelineComponent implements OnInit {
  @Input() adrs: ADR[] = [];
  @Input() importTaskId = 0;

  constructor(private adrService: AdrService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.loadCommitData();
  }

  /**
   * get commit data for each ADR
   */
  loadCommitData() {
    for (let adr of this.adrs) {
      this.adrService.getCommitData(this.importTaskId, adr.filePath)
        .subscribe({
          next: commit => {
            console.log('Commit data received: ', commit);
            if (commit) {
              adr.date = commit.committedDate;
              console.log(adr.title);
            } else {
              console.log("Commit data could not be retrieved")
            }
          },
          error: (error) => {
            console.log('Get Commit data failed:', error);
          }
        });
    }
  }
}
