import { HttpRequest, HttpResponse } from '@angular/common/http';

/**
 * Abstract class to define caching operations
 */
export abstract class Cache {
  /**
   * fetch response for a specific request from cache
   * @param req HTTP request
   */
  abstract get(req: HttpRequest<any>): HttpResponse<any> | null;

  /**
   * put response with corresponding request into cache
   * @param req HTTP request
   * @param res HTTP response
   */
  abstract put(req: HttpRequest<any>, res: HttpResponse<any>): void;
}

