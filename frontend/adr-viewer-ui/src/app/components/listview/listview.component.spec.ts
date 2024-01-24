import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';
import {HttpClientModule} from '@angular/common/http';
import {RouterTestingModule} from "@angular/router/testing";
import {ListviewComponent} from './listview.component';
import {ADR} from "../../interfaces/adr";
import {AdrService} from "../../services/adr.service";
import {of} from "rxjs";
import {NgbModule, NgbPaginationModule} from '@ng-bootstrap/ng-bootstrap';
import {Router} from "@angular/router";
import {AdrDetailViewComponent} from "../adr-detail-view/adr-detail-view.component";

describe('ListviewComponent', () => {
  let component: ListviewComponent;
  let fixture: ComponentFixture<ListviewComponent>;
  let router: Router;
  const adrServiceMock = jasmine.createSpyObj('AdrService', ['getAdrs']);
  const routerSpy = jasmine.createSpyObj<Router>('Router', ['navigate']);
  // Mock data for Listview Component
  const mockAdr: ADR = {
    id: 1, title: 'ADR1', date: '01.12.2020',
    status: 'Deprecated', context: 'Context', decision: 'Decision', consequences: 'Consequences',
    commit: 'Commit1', artifacts: [], relations: [],importTaskId: 1, filePath: 'example path'
  };
  const mockAdrs: ADR[] = [
    mockAdr,
    {
      id: 2, title: 'ADR 2', date: '12.12.2020', status: 'Active', context: 'Context 2',
      decision: 'Decision 2', consequences: 'Consequences 2', commit: 'Commit 2',
      artifacts: [], relations: [],importTaskId: 1, filePath: 'example path'
    },
  ];
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientModule, NgbModule, NgbPaginationModule,
        RouterTestingModule.withRoutes([
          { path: 'detailview', component: AdrDetailViewComponent },
        ]),],
      declarations: [ListviewComponent],
      providers: [
        {provide: AdrService, useValue: adrServiceMock},

      ]
    })
      .compileComponents();
    fixture = TestBed.createComponent(ListviewComponent);
    adrServiceMock.getAdrs.and.returnValue(of({data: mockAdrs, paginationInfo: {pageOffset: 0, pageCount: 1}}));
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load the ADRs', () => {
    adrServiceMock.getAdrs.and.returnValue(of({data: mockAdrs, paginationInfo: {pageOffset: 0, pageCount: 1}}));
    component.loadPage('searchText', 0, 8);
    expect(component.adrs).toEqual(mockAdrs);
  });
});
