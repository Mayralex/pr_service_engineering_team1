import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpClientModule} from '@angular/common/http';
import {RouterTestingModule} from "@angular/router/testing";
import {AdrDetailViewComponent} from './adr-detail-view.component';
import {AdrService} from "../../services/adr.service";
import {of} from 'rxjs';
import {ADR} from "../../interfaces/adr";
import {Artifact} from "../../interfaces/artifact";
import {Relation} from "../../interfaces/relation";
import {By} from "@angular/platform-browser";
import {DebugElement} from "@angular/core";
import {Router} from "@angular/router";

describe('AdrDetailViewComponent', () => {
  let fixture: ComponentFixture<AdrDetailViewComponent>;
  let component: AdrDetailViewComponent;
  let adrServiceSpy: jasmine.SpyObj<AdrService>;
  let router: Router;
  // Testdata for Mock Service Replies
  const artifacts: Artifact[] = [{id: 1, name: 'Artifact'}];
  const relations: Relation[] = [{id: 1, type: 'enables', affectedAdr: 'ADR2'}]
  const mockAdr: ADR = {
    id: 1, title: 'ADR', date: '01.12.2020',
    status: 'Active', context: 'Context', decision: 'Decision', consequences: 'Consequences',
    commit: 'commit', artifacts: artifacts, relations: relations,
  };
  const mockAdrs: ADR[] = [
    mockAdr,
    {
      id: 2, title: 'ADR 2', date: '12.12.2020', status: 'Deprecated', context: 'Context',
      decision: 'Decision', consequences: 'Consequences', commit: 'commit',
      artifacts: artifacts, relations: [],
    },
  ];

  beforeEach(async () => {
    //Mock ADR-Service
    adrServiceSpy = jasmine.createSpyObj('AdrService', ['getAdrById', 'getAllADRs']);

    await TestBed.configureTestingModule({
      imports: [HttpClientModule, RouterTestingModule],
      declarations: [AdrDetailViewComponent],
      providers: [
        {provide: AdrService, useValue: adrServiceSpy},
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AdrDetailViewComponent);
    component = fixture.componentInstance;
    //Mock ADR Service Responses
    adrServiceSpy.getAdrById.and.returnValue(of(mockAdr))
    adrServiceSpy.getAllADRs.and.returnValue(of(mockAdrs));
    router = TestBed.inject(Router);
    component.ngOnInit();
    fixture.detectChanges();

  });
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch ADR data on initialization', () => {
    expect(component.adrById = mockAdr).toBeTruthy();
  });

  it('should have title Placeholder title', () => {
    const titleElement: DebugElement = fixture.debugElement.query(By.css('#adrTitle'));
    expect(titleElement.nativeElement.textContent.trim()).toBe(component.adrById.title);
  });

  it('should return isActive', () => {
    const statusElement = fixture.debugElement.query(By.css('.status'));
    const activeCircle = statusElement.query(By.css('svg[fill="forestgreen"]'));
    const deprecatedCircle = statusElement.query(By.css('svg[fill="red"]'));
    expect(activeCircle).toBeTruthy();
    expect(deprecatedCircle).toBeFalsy();
  });

  it('should throw error when not finding the ADR to navigate to', () => {
    const consoleSpy = spyOn(console, 'log');
    component.navigateToAdr('Nonexistent ADR');
    expect(consoleSpy).toHaveBeenCalledWith('ADR not found');
  });
  it('should navigate to the related ADR', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.navigateToAdr('ADR 2');
    expect(navigateSpy).toHaveBeenCalledWith(['/detailview'], {
      queryParams: {id: 2},
    });
  })
});
