import {Component} from '@angular/core';
import {Router, NavigationEnd} from '@angular/router';
import {filter} from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  hideNavbar: boolean = true;

  constructor(private router: Router) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(event => {
      // Check if the current route needs to hide the navbar
      // @ts-ignore
      if (event.url === '/') {
        this.hideNavbar = true;
      } else {
        // @ts-ignore
        this.hideNavbar = event.url.includes('home');
      }
    });
  }
}
