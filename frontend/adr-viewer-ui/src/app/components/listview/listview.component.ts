import { Component, OnInit } from '@angular/core';
import {ADR} from "../../interfaces/adr";
import {AdrService} from "../../services/adr.service";
import {MessageService} from "../../services/message.service";

@Component({
  selector: 'app-listview',
  templateUrl: './listview.component.html',
  styleUrls: ['./listview.component.css']
})
export class ListviewComponent implements OnInit {

  selectedADR?: ADR;
  adrs = {} as ADR[];
  adrById = {} as ADR;

  constructor(
    private adrService: AdrService,
    private messageService: MessageService
  ) { }

  ngOnInit(): void {
    //TODO: Change to Input from User Interface
    this.getAllADRs("flohuemer", "graal", "wasm/docs/arch", "adrs");
    this.getAdrById(2);
  }

  onSelect(adr: ADR): void {
    this.selectedADR = adr;
    this.messageService.add(`ADRComponent: Selected adr id=${adr.id}`);
  }

  getAllADRs(repoOwner: string, repoName: string, directoryPath: string, branch: string): void {
    this.adrService.getAllADRs(repoOwner, repoName, directoryPath, branch)
      .subscribe(adrs => this.adrs = adrs);
  }

  getAdrById(id: number): void {
    this.adrService.getAdrById(id)
      .subscribe(adr => this.adrById = adr);
  }
}
