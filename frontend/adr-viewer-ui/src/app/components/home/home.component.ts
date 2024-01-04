import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Location} from "@angular/common";
import {AdrService} from "../../services/adr.service";

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
              private adrService: AdrService) {
  }

  ngOnInit(): void {
    this.userData = {repoOwner: "", repoName: "", directoryPath: "", branch: ""};
    this.hideButton = sessionStorage.getItem('previousProject') !== 'true';
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
        this.router.navigate(['/loading'], {
          queryParams: {
            importTaskId: value.id
          }
        });
      },
      error: err => {
        console.error("An error occured when analysing the repository")
        // TODO: Show error message
      }
    })
  }

  // Used by "Go back to previous project" button to return to the last page
  prevProject(){
    this._location.back();
  }

  // Used for easy access to Graal Project for testing, etc. - can be deleted when not needed anymore
  useGraal() {
    this.userData.repoOwner = "flohuemer";
    this.userData.repoName = "graal";
    this.userData.directoryPath = "wasm/docs/arch";
    this.userData.branch = "adrs";

    this.onSubmit();
  }
}
