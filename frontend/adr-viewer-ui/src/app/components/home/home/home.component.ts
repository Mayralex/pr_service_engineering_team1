import { Component, OnInit } from '@angular/core';
import {ADR} from "../../../interfaces/adr";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  adr: ADR = {
    id: 1,
    title: "Title",
    context: "Context",
    decision: "Decision",
    status: "Status",
    consequences: "Consequences",
    artifacts: "Artifacts",
    relations: "Relations"
  };

  constructor() { }

  ngOnInit(): void {
  }

}
