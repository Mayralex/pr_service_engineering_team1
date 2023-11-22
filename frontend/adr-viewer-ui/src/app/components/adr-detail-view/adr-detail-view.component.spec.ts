import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdrDetailViewComponent } from './adr-detail-view.component';

describe('AdrDetailViewComponent', () => {
  let component: AdrDetailViewComponent;
  let fixture: ComponentFixture<AdrDetailViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdrDetailViewComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdrDetailViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
