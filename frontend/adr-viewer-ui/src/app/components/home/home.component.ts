import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  // User input data
  userData: { repoOwner: string; repoName: string; directoryPath: string; branch: string };

  constructor(private router: Router) {
  }

  ngOnInit(): void {
    this.userData = {repoOwner: "", repoName: "", directoryPath: "", branch: ""};
  }

  /** Navigate to listview component with user information and store input variables
   *
   */
  onSubmit() {
    // store input variables
    localStorage.setItem('repoOwner', JSON.stringify(this.userData.repoOwner));
    localStorage.setItem('repoName', JSON.stringify(this.userData.repoName));
    localStorage.setItem('directoryPath', JSON.stringify(this.userData.directoryPath));
    localStorage.setItem('branch', JSON.stringify(this.userData.branch));

    this.router.navigate(['/listview'], {
      queryParams: {
        repoOwner: this.userData.repoOwner,
        repoName: this.userData.repoName,
        directoryPath: this.userData.directoryPath,
        branch: this.userData.branch,
      }
    });
  }

  // Used for easy access to Graal Project for testing, etc. - can be deleted when not needed anymore
  useGraal() {
    // store input variables
    localStorage.setItem('repoOwner', JSON.stringify("flohuemer"));
    localStorage.setItem('repoName', JSON.stringify("graal"));
    localStorage.setItem('directoryPath', JSON.stringify("wasm/docs/arch"));
    localStorage.setItem('branch', JSON.stringify("adrs"));

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
