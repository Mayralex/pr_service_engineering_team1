import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {ADR} from "../../../interfaces/adr";
import {AdrService} from "../../../services/adr.service";
import * as vis from "vis";

@Component({
  selector: 'app-timeline',
  templateUrl: './timeline.component.html',
  styleUrls: ['./timeline.component.css']
})
export class TimelineComponent implements OnInit {
  @Input() adrs: ADR[] = [];
  @Input() importTaskId = 0;

  @ViewChild('timelineContainer') timelineContainer: ElementRef;

  private timeline: vis.Timeline | undefined

  constructor(private adrService: AdrService) { }

  ngOnInit(): void {
    this.loadCommitDataAndCreateTimeline();
  }

  loadCommitDataAndCreateTimeline()
  {
    this.loadCommitData();
    this.createTimeline();
  };

  createTimeline() {
    if (this.timelineContainer && this.timelineContainer.nativeElement) {
      // DOM element where the Timeline will be attached
      const container = this.timelineContainer.nativeElement;

      const items = new vis.DataSet();
      for (let adr of this.adrs) {
        const item = {
          id: adr.title.split(':')[0],
          content: adr.title,
          start: adr.date,
        }
        items.add(item);
      }

      const options = {
      };

      // Create a Timeline
      this.timeline = new vis.Timeline(container, items, options);
    }
  }

  /**
   * get commit data for each ADR
   */
 loadCommitData() {
    for (let adr of this.adrs) {
      this.adrService.getCommitData(this.importTaskId, adr.filePath)
        .subscribe({
          next: commit => {
            //console.log('Commit data received: ', commit);
            if (commit) {
              const dateTimeString = commit.committedDate;
              const dateString = new Date(dateTimeString).toISOString().slice(0, 10);
              console.log(dateString);
              adr.date = dateString;
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
