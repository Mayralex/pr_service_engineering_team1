import { Injectable } from '@angular/core';
import { HttpRequest, HttpResponse } from '@angular/common/http';
import { Cache } from '../classes/cache';
import { CacheEntry, MAX_CACHE_AGE } from '../interfaces/cache-entry';

/**
 * Service that handles the caching.
 * Includes implementation of PUT and GET methods from abstract class cache,
 * which put HTTP request in the cache and retrieves them when requested.
 */
@Injectable({
  providedIn: 'root'
})
export class CacheMapService implements Cache {
  cacheMap = new Map<string, CacheEntry>();
  get(req: HttpRequest<any>): HttpResponse<any> | null {
    const entry = this.cacheMap.get(req.urlWithParams);
    if (!entry) {
      return null;
    }
    const isExpired = (Date.now() - entry.entryTime) > MAX_CACHE_AGE;
    return isExpired ? null : entry.response;
  }
  put(req: HttpRequest<any>, res: HttpResponse<any>): void {
    const entry: CacheEntry = { url: req.urlWithParams, response: res, entryTime: Date.now() };
    this.cacheMap.set(req.urlWithParams, entry);
    this.deleteExpiredCache();
  }
  private deleteExpiredCache() {
    this.cacheMap.forEach(entry => {
      if ((Date.now() - entry.entryTime) > MAX_CACHE_AGE) {
        this.cacheMap.delete(entry.url);
      }
    })
  }
}

