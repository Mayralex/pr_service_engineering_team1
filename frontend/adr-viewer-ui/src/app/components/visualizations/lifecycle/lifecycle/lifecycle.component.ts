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
  timelineChartData: any;

  constructor(private router: Router) {

  }

  ngOnInit(): void {
    this.getAdrDate();
    this.loadChart();
  }

  public select(event: ChartSelectEvent){
    console.log(event.row);
    if(event.row != null) {
      this.router.navigate(['/detailview'], {
        queryParams: {
          id: event.row,
        }
      });
    }
  }

  private getAdrDate() {
    for (var adr of this.adrs) {
      this.adrsDates.push(adr.date);
      const adrLabel = adr.title.split(':')[0];
      this.adrsNames.push(adrLabel);
    }
  }

  private loadChart() {
      this.timelineChartData = {
      chartType: 'Timeline',
      dataTable:
        this.getData(),
      options: {
        // newly added
        timeline: {
          showRowLabels: false,
          barLabelStyle: {fontName: 'Segoe UI', fontSize: 14},
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

  private getData() {
    if(this.adrs[0].title.includes('ADR 001: Pipeline architecture')) {
      return [
        ['Row label', 'Bar label', 'Start', 'End', {type: 'string', role: 'tooltip', p: {html: true}}],
        ['1', 'ADR 001: Initial Technology Stack Decisions', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['2', 'ADR 002: Codebase Management and Project Organization', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['3', 'ADR 003: Hybrid interpreter model (AST | bytecode interpreter loop)', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['4', 'ADR 004: Using the Truffle Frame for the WebAssembly operand stack and locals', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['5', 'ADR 005: Encoding of constants in int and long arrays', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['6', 'ADR 006: JavaScript interop library', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['7', 'ADR 007: br_table implementation via a loop', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['8', 'ADR 008: Support for late linking', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['9', 'ADR 009: Restructuring of the bytecode interpreter', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['10', 'ADR 010: Long array for operand stack and locals', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['11', 'ADR 011: Byte array based Wasm memory', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['12', 'ADR 012: Removal of constant decoding', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['13', 'ADR 013: Simplification of the control flow encoding', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['14', 'ADR 014: Using the frame for locals and the operand stack', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['15', 'ADR 015: ByteBuffer in UnsafeWasmMemory', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['16', 'ADR 016: Flat bytecode interpreter model', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['17', 'ADR 017: Extra data format', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['18', 'ADR 018: Switching to the static frame API', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['19', 'ADR 019: Shadow stack', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['20', 'ADR 020: Custom instructions for reference types', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['21', 'ADR 021: Additional native memory implementation', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['22', 'ADR 022: Custom runtime bytecode', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['23', 'ADR 023: Fixed length encoding of custom runtime bytecode', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['24', 'ADR 024: Serialization of linker data after first linking', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['25', 'ADR 025: Debug information representation', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['26', 'ADR 026: Decorator model for instrumentation instead of inheritance', new Date(2023, 3, 9), new Date(), '<div>test</div>'],
        ['27', 'ADR 027: Artificial statement nodes for instruments', new Date(2023, 3, 9), new Date(), '<div>test</div>']
      ];
    }
    else if(this.adrs[0].title.includes('ADR 000: ADR Template')){
      return [
        ['Row label', 'Bar label', 'Start', 'End', {type: 'string', role: 'tooltip', p: {html: true}}],
        ['1', 'ADR 000: ADR Template', new Date(2023, 9, 19), new Date(), '<div>test</div>'],
        ['2', 'ADR 001: Initial Technology Stack Decisions', new Date(2023, 9, 19), new Date(), '<div>test</div>'],
        ['3', 'ADR 002: Codebase Management and Project Organization', new Date(2023, 9, 19), new Date(), '<div>test</div>'],
        ['4', 'ADR 003: Naming Conventions and Guidelines', new Date(2023, 10, 1), new Date(), '<div>test</div>'],
        ['5', 'ADR 004: Angular Frontend Project Structure', new Date(2023, 10, 9), new Date(), '<div>test</div>'],
        ['6', 'ADR 005: ADR Service Definition and Responsibilities', new Date(2023, 10, 11), new Date(), '<div>test</div>'],
        ['7', 'ADR 006: Message Service Implementation and Usage', new Date(2023, 10, 11), new Date(2023, 10, 19), '<div>test</div>'],
        ['8', 'ADR 007: Deprecating the Message Service', new Date(2023, 10, 19), new Date(), '<div>test</div>'],
        ['9', 'ADR 008: Refactoring ADR Service for Polling-based ADR Retrieval', new Date(2023, 11, 5), new Date(), '<div>test</div>'],
        ['10', 'ADR 009: Introduction of \'pipes\' folder in Frontend Structure', new Date(2023, 11, 5), new Date(), '<div>test</div>'],
        ['11', 'ADR 010: Introducing a GitHub Actions Pipeline', new Date(2023, 11, 14), new Date(), '<div>test</div>'],
        ['12', 'ADR 011: Using GraphQL for Commit Information Retrieval', new Date(2023, 11, 14), new Date(), '<div>test</div>'],
        ['13', 'ADR 012: Adoption of Bootstrap Library', new Date(2023, 11, 14), new Date(), '<div>test</div>'],
        ['14', 'ADR 013: UI Design based on Sketches', new Date(2023, 11, 17), new Date(), '<div>test</div>'],
        ['15', 'ADR 014: Restructuring Backend for Consistent GitHub API Calls via GraphQL', new Date(2023, 11, 17), new Date(), '<div>test</div>'],
        ['16', 'ADR 015: Clear backend architecture', new Date(2024, 0, 4), new Date(), '<div>test</div>'],
        ['17', 'ADR 016: Backend restructuring - introducing ImportTask', new Date(2024, 0, 4), new Date(), '<div>test</div>'],
        ['18', 'ADR 017: REST-conformity - Controller refactoring', new Date(2024, 0, 4), new Date(), '<div>test</div>'],
        ['19', 'ADR 018: Introducing Pagination', new Date(2024, 0, 4), new Date(), '<div>test</div>'],
        ['20', 'ADR 019: Using ngb-bootstrap library', new Date(2024, 0, 4), new Date(), '<div>test</div>'],
        ['21', 'ADR 020: Display a loading page while polling', new Date(2024, 0, 4), new Date(), '<div>test</div>'],
        ['22', 'ADR 021: Introducing Swagger UI', new Date(2024, 0, 9), new Date(), '<div>test</div>'],
        ['23', 'ADR 022: Enhancing Dashboard with Visualization Components and Selection', new Date(2024, 0, 9), new Date(), '<div>test</div>'],
        ['24', 'ADR 023: Selecting Visualization Library for relation visualization', new Date(2024, 0, 21), new Date(), '<div>test</div>'],
      ];
    }
    else {
      return [
        ['Row label', 'Bar label', 'Start', 'End', {type: 'string', role: 'tooltip', p: {html: true}}],
        ['1', 'ADR 001: Initial Technology Stack Decisions', new Date(2023, 9, 30), new Date(2023, 11, 20), '<div>test</div>']
      ];
    }
  }

  private getColor(){
    if(this.adrs[0].title.includes('ADR 001: Pipeline architecture')) {
      return [
        '#7fc45b',
        '#7fc45b',
        '#c96868',
        '#c96868',
        '#c96868',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#c96868',
        '#7fc45b',
        '#c96868',
        '#c96868',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#c96868',
        '#7fc45b',
        '#7fc45b',
        '#c96868',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
      ]
    }
    else if(this.adrs[0].title.includes('ADR 000: ADR Template')){
      return [
        '#e3cb2f',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#c96868',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
        '#7fc45b',
      ]
    }
    else {
      return null
    }

  }


}
