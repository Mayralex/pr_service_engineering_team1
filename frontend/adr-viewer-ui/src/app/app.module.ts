import { NgModule } from '@angular/core';
import {BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from "@angular/common/http";
import { NgFor } from "@angular/common";

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ListviewComponent} from './components/listview/listview.component';
import { HomeComponent } from './components/home/home.component';
import { NavComponent } from './components/nav/nav.component';
import { AdrDetailViewComponent } from './components/adr-detail-view/adr-detail-view.component';
import { FormsModule } from "@angular/forms";
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { NgChartsModule } from "ng2-charts";
import { SearchPipe } from './pipes/search.pipe';

@NgModule({
  declarations: [
    AppComponent,
    ListviewComponent,
    HomeComponent,
    NavComponent,
    AdrDetailViewComponent,
    DashboardComponent,
    SearchPipe
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    HttpClientModule,
    NgFor,
    FormsModule,
    NgChartsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
