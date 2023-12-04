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
    this.userData = {repoOwner: '', repoName: '', directoryPath: '', branch: ''};
  }

  /** Navigate to listview component with user information
   *
   */
  onSubmit() {
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
