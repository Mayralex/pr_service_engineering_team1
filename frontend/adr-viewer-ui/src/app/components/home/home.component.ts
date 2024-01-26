import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Location} from "@angular/common";
import {AdrService} from "../../services/adr.service";
import {LoadingService} from "../../services/loading.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  // User input data
  userData: { repoOwner: string; repoName: string; directoryPath: string; branch: string };

  // hide previous project button
  hideButton: boolean = true;

  constructor(private router: Router,
              private _location: Location,
              private adrService: AdrService,
              private loadingService: LoadingService
  ) {
  }

  ngOnInit(): void {
    this.userData = {repoOwner: "", repoName: "", directoryPath: "", branch: ""};
    this.hideButton = sessionStorage.getItem("previousProject") !== '"true"';
  }

  /** Navigate to listview component with user information and store input variables
   *
   */
  onSubmit() {
    this.adrService.analyseRepository(
      this.userData.repoOwner,
      this.userData.repoName,
      this.userData.directoryPath,
      this.userData.branch
    ).subscribe({
      next: value => {
        // disable navbar buttons while loading
        this.loadingService.toggleLoadingStatus();

        sessionStorage.setItem("previousProject", JSON.stringify("true"));
        sessionStorage.setItem("importTaskId", JSON.stringify(value.id));
        sessionStorage.setItem("repoOwner", JSON.stringify(this.userData.repoOwner));
        sessionStorage.setItem("repoName", JSON.stringify(this.userData.repoName));
        sessionStorage.setItem("directoryPath", JSON.stringify(this.userData.directoryPath));
        sessionStorage.setItem("branch", JSON.stringify(this.userData.branch));
        this.router.navigate(['/loading'], {
          queryParams: {
            importTaskId: value.id
          }
        });
      },
      error: err => {
        console.error("An error occured when analysing the repository")
      }
    })
  }

  /** Used by "Go back to previous project" button to return to the last page
   *
   */
  prevProject(){
    this._location.back();
  }

  // Used for easy access to Graal Project for testing, etc. - can be deleted when not needed anymore
  useExample() {
    this.userData.repoOwner = "mayralex";
    this.userData.repoName = "pr_service_engineering_team1";
    this.userData.directoryPath = "docs/adrs";
    this.userData.branch = "main";

    this.onSubmit();
  }

}
