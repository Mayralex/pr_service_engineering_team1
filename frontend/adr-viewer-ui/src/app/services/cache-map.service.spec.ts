import { TestBed } from '@angular/core/testing';

import { CacheMapService } from './cache-map.service';

describe('CacheMapService', () => {
  let service: CacheMapService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CacheMapService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
