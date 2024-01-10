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
import {BarchartComponent} from "./components/visualizations/barchart/barchart.component";
import { LinechartComponent } from './components/visualizations/linechart/linechart.component';
import { SearchPipe } from './pipes/search.pipe';
import { LoadingPageComponent } from './components/loading-page/loading-page.component';
import { RelationGraphComponent } from './components/visualizations/relation-graph/relation-graph.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {PiechartComponent} from "./components/visualizations/piechart/piechart.component";

@NgModule({
  declarations: [
    AppComponent,
    ListviewComponent,
    HomeComponent,
    NavComponent,
    AdrDetailViewComponent,
    DashboardComponent,
    BarchartComponent,
    LinechartComponent,
    SearchPipe,
    LoadingPageComponent,
    RelationGraphComponent,
    PiechartComponent,
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        NgbModule,
        HttpClientModule,
        NgFor,
        FormsModule,
        NgChartsModule,
        BrowserAnimationsModule,
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
