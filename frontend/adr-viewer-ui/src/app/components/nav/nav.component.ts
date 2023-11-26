import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.css']
})
export class NavComponent implements OnInit {

  // title on the left of navbar
  title = "ADR-Viewer";

  // list of items in the navbar, including route and name - can be added/removed here
  navbarItems = [
    ["/home", "Home"],
    ["/listview", "ADR-List"],
    ["", "Dashboard"]
  ];
  constructor() { }

  ngOnInit(): void {
  }

}
