import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ListviewComponent} from "./components/listview/listview.component";
import {HomeComponent} from "./components/home/home.component";
import {AdrDetailViewComponent} from "./components/adr-detail-view/adr-detail-view.component";
import {DashboardComponent} from "./components/dashboard/dashboard.component";
import {LoadingPageComponent} from "./components/loading-page/loading-page.component";

const routes: Routes = [
  { path: 'listview', component: ListviewComponent },
  { path: 'home', component: HomeComponent },
  { path: 'detailview', component: AdrDetailViewComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'loading', component: LoadingPageComponent },
  { path: '', redirectTo: '/home', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
