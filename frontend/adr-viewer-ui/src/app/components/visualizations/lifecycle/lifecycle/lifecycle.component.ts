import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ADR} from "../../../../interfaces/adr";
import {BaseChartDirective} from "ng2-charts";
import {ChartSelectEvent} from 'ng2-google-charts';
import {Router} from "@angular/router";

@Component({
  selector: 'app-lifecycle',
  templateUrl: './lifecycle.component.html',
  styleUrls: ['./lifecycle.component.css']
})
export class LifecycleComponent implements OnInit {
  @Input() adrs: ADR[] = [];
  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  adrsDates : string[] = [];
  adrsNames : string[] = [];

  constructor(private router: Router) {

  }

  ngOnInit(): void {
    this.getAdrDate();
  }

  public select(event: ChartSelectEvent){
    console.log(event.row);
    if(event.row != null) {
      this.router.navigate(['/detailview'], {
        queryParams: {
          id: event.row+1,
        }
      });
    }
  }

  private getAdrDate() {
    //TODO: wenn date und commit history von backend verfügbar sind, müssen diese hier zu den adrs geladen werden und die beispiel daten ersetzen
    for (var adr of this.adrs) {
      this.adrsDates.push(adr.date);
      const adrLabel = adr.title.split(':')[0];
      this.adrsNames.push(adrLabel);
    }
  }

  public timelineChartData:any =  {
    chartType: 'Timeline',
    dataTable:
      [
        ['Row label', 'Bar label', 'Start', 'End', {type: 'string', role: 'tooltip', p: {html: true}}],
        [ '1', 'ADR 001: Initial Technology Stack Decisions', new Date(2023, 9, 30), new Date(2023, 11, 20), '<div>test</div>' ],
        [ '2', 'ADR 002: Codebase Management and Project Organization', new Date(2023, 10, 25), new Date(2024, 0, 31), '<div>test</div>' ],
        [ '3', 'ADR 003: Naming Conventions and Guidelines',  new Date(2024, 0, 10), new Date(2024, 0, 31), '<div>test</div>' ]
      ],
    options: {
      // newly added
      timeline: {
        showRowLabels: false,
        barLabelStyle: { fontName: 'Segoe UI', fontSize: 14 },
      },
      fontName: 'Segoe UI',
      // Pre determined
      //focusTarget: 'category',
      animation: {
        startup: true,
        easing: 'out',
        duration: 500,
      },
      tooltip: {
        isHtml: true
      },
      height: '250',
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
      colors: [ // use for coloring active green and deprecated red --> need to process adrs and save color array here
        '#c96868',
        '#7fc45b',
        '#7fc45b',
      ],
      bar: {
        groupWidth: '35'
      },
      legend: {
        position: 'left'
      },
    }
  }
}
