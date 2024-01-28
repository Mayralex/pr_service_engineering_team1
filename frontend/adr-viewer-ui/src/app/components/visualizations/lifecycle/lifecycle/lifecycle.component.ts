import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ADR} from "../../../../interfaces/adr";
import {BaseChartDirective} from "ng2-charts";
import {ChartSelectEvent} from 'ng2-google-charts';
import {Router} from "@angular/router";
import {AdrService} from "../../../../services/adr.service";
import {CommitData} from "../../../../interfaces/commitData";

/**
 * This component renders the lifecycle visualization for ADRs
 */
@Component({
  selector: 'app-lifecycle',
  templateUrl: './lifecycle.component.html',
  styleUrls: ['./lifecycle.component.css']
})
export class LifecycleComponent implements OnInit {
  @Input() adrs: ADR[] = [];
  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  // arrays containing the data for the chart
  adrsData : any[] = [];
  colorData : string[] = [];

  // variable containing chart config
  timelineChartData: any;

  importTaskId: number;

  // value for progress bar
  loadingProgess: number;

  // component cannot be rendered for private repositories
  privateRepo: boolean;

  constructor(private router: Router, private adrService: AdrService) {
  }

  ngOnInit(): void {
    // @ts-ignore
    this.importTaskId = +sessionStorage.getItem('importTaskId');
    this.loadingProgess = 0;
    this.getAdrData().then(r => this.loadChart());
  }

  private getData() {
    return this.adrsData;
  }

  private getColor(){
    return this.colorData;
  }

  /** On selection of an ADR in the chart, go to the respective detail view
   *
   * @param event
   */
  public select(event: ChartSelectEvent){
    if(event.row != null) {
      let id = +event.selectedRowValues[0];
      this.router.navigate(['/detailview'], {
        queryParams: {
          id: id,
        }
      });
    }
  }

  /** Fetch the commit history for each adr, load ADR id, ADR title, the first commit and the last commit into the chart. Each ADR is colored by ADR status.
   *
   * @private
   */
  private async getAdrData() {

    // config for chart data
    this.adrsData.push(['Row label', 'Bar label', 'Start', 'End', {type: 'string', role: 'tooltip', p: {html: true}}]);

    let counter = 1;
    try {

      for (var adr of this.adrs) {
        this.loadingProgess = (counter / this.adrs.length) * 100;
        counter = counter+1;

        let commitData: CommitData[] | undefined = await this.adrService.getCommitData(
          this.importTaskId, adr.filePath
        ).toPromise();

        if (commitData) {
          // sort commits by date
          commitData = commitData.sort((a, b) => {
            let aTime = new Date(a.committedDate);
            let bTime = new Date(b.committedDate);
            return aTime.getTime() - bTime.getTime();
          });

          // get last date or current date if still active
          let lastDate: Date;
          if(commitData.length <= 1){
            lastDate = new Date;
          }
          else if(commitData[commitData.length-1].status.toLowerCase() != 'active') {
            lastDate = new Date(commitData[commitData.length - 1].committedDate);
          }
          else {
            lastDate = new Date;
          }

          this.adrsData.push([adr.id.toString(), adr.title, new Date(commitData[0].committedDate), new Date(lastDate), '<div>test</div>']);

          // color for status
          if(adr.status.toLowerCase() === 'active'){
            this.colorData.push('#7fc45b');
          }
          else if(adr.status.toLowerCase() === 'deprecated'){
            this.colorData.push('#c96868');
          }
          else{
            this.colorData.push('#e3cb2f');
          }

        }

      }
    }
    catch (error){
      console.log('Error: Cannot fetch private repository', error);
      this.privateRepo = true;
    }
  }

  /** configurations for the gantt chart
   *
   * @private
   */
  private loadChart() {
      this.timelineChartData = {
      chartType: 'Timeline',
      dataTable:
        this.getData(),
      options: {
        timeline: {
          showRowLabels: false,
          barLabelStyle: {fontName: 'Segoe UI', fontSize: 14},
        },
        fontName: 'Segoe UI',
        animation: {
          startup: true,
          easing: 'out',
          duration: 500,
        },
        tooltip: {
          isHtml: true
        },
        height: '1500',
        interpolateNulls: true,
        chartArea: {
          width: '90%',
          height: '79%',
        },
        vAxes: {
          0: {
            titlePosition: 'none',
            textStyle: {
              color: '#febd02',
              bold: true,
              fontSize: 13,
            },
            format: '#',
            gridlines: {
              color: '#eaeaea',
              count: '5',
            },
          },
          1: {
            titlePosition: 'none',
            format: '#',
            gridlines: {
              color: 'transparent'
            },
          },
          2: {
            groupWidth: '100%',
            titlePosition: 'none',
            textStyle: {
              color: '#0284ff',
              bold: true,
              fontSize: 13,
            },
            format: 'decimal',
            gridlines: {
              color: 'transparent'
            },
          },
        },
        hAxis: {
          title: 'Time',
          textStyle: {
            color: '#393939',
            bold: true,
            fontSize: 13,
          },
          format: 'MMM y',
          gridlines: {
            count: 0,
            color: 'transparent'
          },
          ticks: [],
        },
        series: {
          0: {
            targetAxisIndex: 0,
            type: 'area',
          },
          1: {
            type: 'line'
          },
          2: {
            targetAxisIndex: 2,
            type: 'bars',
            dataOpacity: 0.5,
          },
        },
        colors: this.getColor(),
        bar: {
          groupWidth: '35'
        },
        legend: {
          position: 'left'
        },
      }
    }
  }

}
