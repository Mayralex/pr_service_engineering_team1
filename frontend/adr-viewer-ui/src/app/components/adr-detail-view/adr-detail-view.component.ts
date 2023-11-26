import { Component, OnInit } from '@angular/core';
import {ADR} from "../../interfaces/adr";
import {AdrService} from "../../services/adr.service";
import {MessageService} from "../../services/message.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-adr-detail-view',
  templateUrl: './adr-detail-view.component.html',
  styleUrls: ['./adr-detail-view.component.css']
})
export class AdrDetailViewComponent implements OnInit {

  id: number;
  adrById = {} as ADR;

  constructor(
    private adrService: AdrService,
    private messageService: MessageService,
    private route: ActivatedRoute,
  ) { }

  ngOnInit(): void {
    //TODO: Exception handling für z.B. id bleibt -1
    this.id = -1;
    this.route.queryParams.subscribe(params => {
      this.id = params['id'];
    });
    this.getAdrById(this.id);
  }

  getAdrById(id: number): void {
    this.adrService.getAdrById(id)
      .subscribe(adr => this.adrById = adr);
  }
}
