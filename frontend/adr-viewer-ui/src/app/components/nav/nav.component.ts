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
  isChecked: boolean = false;

  constructor() { }

  ngOnInit(): void {
    this.repoData = {repoOwner: "", repoName: "", directoryPath: "", branch: ""};
    // access repo data
    this.repoData.repoOwner = sessionStorage.getItem('repoOwner');
    this.repoData.repoName = sessionStorage.getItem('repoName');
    this.repoData.directoryPath = sessionStorage.getItem('directoryPath');
    this.repoData.branch = sessionStorage.getItem('branch');
  }

  /**
   * Method for changing the theme in the index.html according to the toggle switch button
   * @param $event
   */
  setTheme($event: any) {
    if (this.isChecked) {
      (document.getElementById("app") as HTMLElement).setAttribute('data-bs-theme', 'dark');
      (document.getElementById("moon") as HTMLElement).setAttribute('display', 'none');
      (document.getElementById("sun") as HTMLElement).setAttribute('display', 'center');
    } else {
      (document.getElementById("app") as HTMLElement).setAttribute('data-bs-theme', 'light');
      (document.getElementById("sun") as HTMLElement).setAttribute('display', 'none');
      (document.getElementById("moon") as HTMLElement).setAttribute('display', 'center');
    }
  }
}
