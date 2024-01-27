import { HttpResponse } from '@angular/common/http';

export interface CacheEntry {
  url: string;
  response: HttpResponse<any>
  entryTime: number;
}

/**
 * Time until cache expires in milliseconds
 */
export const MAX_CACHE_AGE = 2000000;

