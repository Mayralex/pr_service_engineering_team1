import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  userData: { repoOwner: string; repoName: string; directoryPath: string; branch: string };

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.userData = { repoOwner: '', repoName: '' , directoryPath: '' , branch: '' };
  }

  onSubmit() {
    // Navigate to target component with user information
    this.router.navigate(['/listview'], {
      queryParams:{
        repoOwner: this.userData.repoOwner,
        repoName: this.userData.repoName,
        directoryPath: this.userData.directoryPath,
        branch: this.userData.branch,
      }
    });
  }
}
