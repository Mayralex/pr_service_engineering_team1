import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Location} from "@angular/common";

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

  constructor(private router: Router, private _location: Location) {
  }

  ngOnInit(): void {
    this.userData = {repoOwner: "", repoName: "", directoryPath: "", branch: ""};
    if(sessionStorage.getItem('previousProject') === 'true'){
      this.hideButton = false;
    }
    else{
      this.hideButton = true;
    }
  }

  /** Navigate to listview component with user information and store input variables
   *
   */
  onSubmit() {
    // store session
    sessionStorage.setItem('previousProject', 'true');

    // store input variables
    sessionStorage.setItem('repoOwner', JSON.stringify(this.userData.repoOwner));
    sessionStorage.setItem('repoName', JSON.stringify(this.userData.repoName));
    sessionStorage.setItem('directoryPath', JSON.stringify(this.userData.directoryPath));
    sessionStorage.setItem('branch', JSON.stringify(this.userData.branch));

    this.router.navigate(['/listview'], {
      queryParams: {
        repoOwner: this.userData.repoOwner,
        repoName: this.userData.repoName,
        directoryPath: this.userData.directoryPath,
        branch: this.userData.branch,
      }
    });
  }

  // Used by "Go back to previous project" button to return to the last page
  prevProject(){
    this._location.back();
  }

  // Used for easy access to Graal Project for testing, etc. - can be deleted when not needed anymore
  useGraal() {
    // store session
    sessionStorage.setItem('previousProject', 'true');

    // store input variables
    sessionStorage.setItem('repoOwner', JSON.stringify("flohuemer"));
    sessionStorage.setItem('repoName', JSON.stringify("graal"));
    sessionStorage.setItem('directoryPath', JSON.stringify("wasm/docs/arch"));
    sessionStorage.setItem('branch', JSON.stringify("adrs"));

    // Navigate to target component with user information
    this.router.navigate(['/listview'], {
      queryParams: {
        repoOwner: "flohuemer",
        repoName: "graal",
        directoryPath: "wasm/docs/arch",
        branch: "adrs",
      }
    });
  }
}
