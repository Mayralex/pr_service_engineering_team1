import { TestBed } from '@angular/core/testing';

import { AdrService } from './adr.service';
import {HttpClientModule} from "@angular/common/http";

describe('AdrService', () => {
  let service: AdrService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule]
    });
    service = TestBed.inject(AdrService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
