import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.css']
})
export class NavComponent implements OnInit {

  // title on the left of navbar
  title = "ADR-Viewer";

  // route to input mask
  home = ["/home", "Change Project"];

  // list of items in the navbar, including route and name - can be added/removed here
  navbarItems = [
    ["/listview", "ADR-List"],
    ["/dashboard", "Dashboard"]
  ];

  repoData: { repoOwner: any; repoName: any; directoryPath: any; branch: any };

  constructor() { }

  ngOnInit(): void {
    this.repoData = {repoOwner: "", repoName: "", directoryPath: "", branch: ""};
    // access repo data
    this.repoData.repoOwner = sessionStorage.getItem('repoOwner');
    this.repoData.repoName = sessionStorage.getItem('repoName');
    this.repoData.directoryPath = sessionStorage.getItem('directoryPath');
    this.repoData.branch = sessionStorage.getItem('branch');
  }

}
