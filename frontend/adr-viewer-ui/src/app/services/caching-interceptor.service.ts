import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpResponse, HttpHandler } from '@angular/common/http';
import { of } from 'rxjs';
import { tap } from 'rxjs/operators';
import { CacheMapService } from './cache-map.service';

// allowed cachable urls
const CACHABLE_URL = "/api/v2/getHistory";
const CACHABLE_URL2 = "/api/v2/import_task";

/**
 * Service which intercepts outgoing HTTP requests and checks if there are already responses stored in the cache.
 * Handles the HTTP request accordingly.
 */
@Injectable({
  providedIn: 'root'
})
export class CachingInterceptor implements HttpInterceptor {
  constructor(private cache: CacheMapService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    if (!this.isRequestCachable(req)) {
      return next.handle(req);
    }
    const cachedResponse = this.cache.get(req);
    if (cachedResponse !== null) {
      return of(cachedResponse);
    }
    return next.handle(req).pipe(
      tap(event => {
        if (event instanceof HttpResponse) {
          this.cache.put(req, event);
        }
      })
    );
  }
  private isRequestCachable(req: HttpRequest<any>) {
    return ((req.method === 'GET') && (req.url.indexOf(CACHABLE_URL) > -1)) || ((req.method === 'POST') && (req.url.indexOf(CACHABLE_URL2) > -1)) ;
  }
}
